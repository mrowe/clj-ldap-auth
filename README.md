# clj-ldap-auth

A library that provides authentication via an LDAP server

## Installation

Add the following dependency to your `project.clj` file:

    [clj-ldap-auth "0.1.1"]

## Example

```clojure
(require '[clj-ldap-auth.ldap :as ldap])

(if (ldap/bind? username password)
  (do something-great)
  (unauthorised))
```

Then start your app with the appropriate system properties set:

```
java -Dauth.hostname=ldap.mydomain.com \
     -Dauth.basedn=dc=mydomain,dc=com
     -Dauth.binddn=uid=myapp,dc=apps,dc=mydomain,dc=com \
     -Dauth.bindpw=supersecret \
     my.program
```

### Configuration

All relevant configuration properties can be set via system properties
(i.e. `java -D...`). The following parameters are required:

 * `auth.hostname` - The hostname of your LDAP server.

 * `auth.basedn` - The base DN in which to search for user ids.

 * `auth.binddn` - The DN with which to bind to the LDAP server to look up usernames.

 * `auth.bindpw` - The password for the `binddn`.

The following parameters are optional:

 * `auth.port` - The port on which to connect to the LDAP server. Defaults to `636`.

 * `auth.ssl` - Should the connection to the LDAP server use SSL. Defaults to `true`.


## Documentation

* [API docs](http://realestate-com-au.github.com/clj-ldap-auth/)


## Bugs

 * Bind DN/password are not optional (i.e. anonymous binds are not supported)
 * Username search field is not configurable (hard coded to `uid`)


## History

### 0.1.1

 * Initial release


## License

Copyright (C) 2013 REA Group Ltd.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
