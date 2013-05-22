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
  (if ssl?
    (LDAPConnection. ssl-socket-factory host port bind-dn bind-pw)
    (LDAPConnection. host port bind-dn bind-pw)))

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
  [results] (.get (.getSearchEntries results) 0))

(defn- dn
  "Returns the DN attribute from the first result in results"
  [results] (.getDN (first-result results)))

(defn- search
  "Search for username using the supplied connection to the LDAP server"
  [connection username]
  (.search connection (:base-dn config) SearchScope/SUB (uid-filter username) nil))
  
(defn bind?
  "Attempts to authenticated with the LDAP server using the supplied
   username and password. Returns true iff successful.

   Any exceptions that occur communicating with the LDAP server (e.g.
   invalid bind dn/password) are ignored and false is returned."
  [username password]
  (let [connection (connect config)
        results (search connection username)]
    (if (results-empty? results)
      false
      (try
        (connect (assoc config :bind-dn (dn results) :bind-pw password))
        true
        (catch LDAPException e
          false)))))
