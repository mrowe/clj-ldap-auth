(defproject clj-ldap-auth "0.1.1"
  :description "A library that provides authentication via an LDAP server"
  :url "http://github.com/realestate-com-au/clj-ldap-auth"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [com.unboundid/unboundid-ldapsdk "2.3.3"]]
  :plugins [[codox "0.6.1"]
            [lein-midje "3.0.0"]]
  :profiles {:dev {:dependencies [[midje "1.5.0"]]}})
