(ns clj-ldap-auth.ldap
  (:import
   com.unboundid.ldap.sdk.LDAPConnection
   com.unboundid.ldap.sdk.LDAPException
   com.unboundid.ldap.sdk.SearchScope
   com.unboundid.util.ssl.SSLUtil
   com.unboundid.util.ssl.TrustAllTrustManager))

(def config {:host (System/getProperty "auth.hostname")
             :port (Integer/parseInt (System/getProperty "auth.port" "636"))
             :ssl? (Boolean/parseBoolean (System/getProperty "auth.ssl" "true"))
             :bind-dn (System/getProperty "auth.binddn")
             :bind-pw (System/getProperty "auth.bindpw")
             :base-dn (System/getProperty "auth.basedn")})

(def ssl-socket-factory (.createSSLSocketFactory (SSLUtil. (TrustAllTrustManager.))))

(defn- connect
  "Returns an authenticated connection to the LDAP server"
  [{:keys [host port ssl? bind-dn bind-pw]}]
  (let [connection (LDAPConnection.)]
    (if ssl?
      (.setSocketFactory connection ssl-socket-factory))
    (.connect connection host port)
    (if (not (nil? bind-dn))
      (.bind connection bind-dn bind-pw))
    connection))

(defn- uid-filter
  "Constructs an LDAP search filter for username"
  [username]
  (str "(uid=" username ")"))

(defn- results-empty?
  "Are there any results?"
  [results]
  (= 0 (.getEntryCount results)))

(defn- first-result
  "Returns the first result in results"
  [results]
  (.get (.getSearchEntries results) 0))

(defn- dn
  "Returns the DN attribute from the first result in results.

   Returns nil if the DN looks empty (since bind with an empty string
   always succeeds)."
  [results]
  (let [dn (.getDN (first-result results))]
    (if (empty? (.trim dn))
      nil
      dn)))

(defn- search
  "Search for username using the supplied connection to the LDAP server"
  [connection username]
  (.search connection (:base-dn config) SearchScope/SUB (uid-filter username) nil))

(defn- fail
  [sink message]
  (do
    (sink message)
    false))

(defn- try-bind
  [username password sink]
  (try
    (connect (assoc config :bind-dn username :bind-pw password))
    true
    (catch LDAPException e
      (fail sink (str "Failed to authenticate '" username "': " (.getMessage e))))))

(defn bind?
  "Attempts to authenticated with the LDAP server using the supplied
   username and password. Returns true iff successful.

   Any exceptions that occur communicating with the LDAP server (e.g.
   invalid bind dn/password) are ignored and false is returned."
  ([username password]
     (bind? username password (fn [message])))
  ([username password sink]
     (let [fail (partial fail sink)]
       (try
         (let [connection (connect config)
               results (search connection username)]
           (if (results-empty? results)
             (fail (str "username '" username "' not found"))
             (if-let [dn (dn results)]
               (try-bind dn password sink)
               (fail (str "Could not find a DN for username '" username "'")))))
         (catch Throwable e
           (fail (str "Error connecting to LDAP server '" (:host config) "': " (.getMessage e))))))))
