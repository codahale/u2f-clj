# u2f-clj

A Clojure library for implementing FIDO U2F multi-factor authentication.

## Usage

```clojure
(require '[u2f-clj.core :as u2f])

;; create a challenge to register a new device
(def reg-challenge (u2f/start-registration "https://example.com/" []))

;; register a new device given a token response
(def device (u2f/finish-registration (:data reg-challenge) reg-response))

;; create a challenge to authenticate with a registered device
(def auth-challenge (u2f/start-authentication "https://example.com/" [device]))

;; verify a response from the token
(when (u2f/finish-authentication [device] (:data auth-challenge] auth-response))
  (println "yay"))
```

## License

Copyright © 2016 Coda Hale

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
