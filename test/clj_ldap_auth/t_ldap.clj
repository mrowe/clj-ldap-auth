(ns clj-ldap-auth.t-ldap
  (:use midje.sweet)
  (:require [clj-ldap-auth.ldap :as ldap]))

(facts "about uid-filter"
  (fact "constructs a filter" (ldap/uid-filter "foo") => "(uid=foo)"))

(facts "about dn"

  (defn result
    [dn]
    (reify com.unboundid.ldif.LDIFRecord
      (getDN [this] dn)))

  (fact "gets dn from search results" (ldap/dn (result "foo")) => "foo")
  (fact "returns nil for empty string" (ldap/dn (result "")) => nil)
  (fact "returns nil for a string contain blanks" (ldap/dn (result " ")) => nil))

(facts "about fail"

  (fact "fail always returns false"
    (ldap/fail (constantly nil) "foo") => false
    (ldap/fail (constantly nil) "") => false
    (ldap/fail (constantly nil) nil) => false)

  (let [result (atom nil)] (ldap/fail #(reset! result %1) "foo")
    (fact "fail calls sink with message" @result => "foo")))
