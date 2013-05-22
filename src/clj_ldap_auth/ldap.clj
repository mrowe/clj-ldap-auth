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
  [{:keys [host port ssl? bind-dn bind-pw]}]
  (if ssl?
    (LDAPConnection. ssl-socket-factory host port bind-dn bind-pw)
    (LDAPConnection. host port bind-dn bind-pw)))

(defn- uid-filter [username] (str "(uid=" username ")"))
(defn- results-empty? [results] (= 0 (.getEntryCount results)))
(defn- first-result [results] (.get (.getSearchEntries results) 0))
(defn- dn [results] (.getDN (first-result results)))

(defn- search
  [connection username]
  (.search connection (:base-dn config) SearchScope/SUB (uid-filter username) nil))
  
(defn bind?
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
