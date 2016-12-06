(ns u2f-clj.core
  "A set of utility functions for FIDO U2F support.

  To register a U2F device, we perform the following:

  1. The server looks up existing devices for the user, and generates a
     registration challenge. It stores a copy of the challenge, and passes it to
     the browser.
  2. In the browser, the U2F Javascript passes this to the token, and the
     token generates a response, which the browser sends to the server.
  3. The server looks up the challenge by ID, verifies that it has not expired,
     and uses both it and the token response to generate a registration, which
     it stores.
  4. The server deletes the registration challenge.
  5. The device is now registered.

  To log in, we perform the following:

  1. The server looks up existing devices for the user, and generates an
     authentication challenge. It stores a copy of the challenge, and passes it
     to the browser.
  2. In the browser, the U2F Javascript passes this to the token, and the token
     generates a response, which the browser sends to the server.
  3. The server looks up the challenge by ID, verifies that it has not expired,
     and uses both it and the token response to verify the token.
  4. The server deletes the authentication challenge.
  5. The user is now authenticated.
  "
  (:import (com.yubico.u2f U2F)
           (com.yubico.u2f.data DeviceRegistration)
           (com.yubico.u2f.data.messages AuthenticateResponse
                                         AuthenticateRequestData
                                         RegisterRequestData
                                         RegisterResponse)
           (com.yubico.u2f.exceptions DeviceCompromisedException
                                      U2fBadInputException)))

(def ^:private ^U2F u2f (U2F.))

(defn- parse-devices
  [devices]
  (mapv #(DeviceRegistration/fromJson %) devices))

(defn start-registration
  "Given a user's existing devices, generates a new device registration
  challenge."
  [app-id devices]
  (let [data (.startRegistration u2f app-id (parse-devices devices))]
    {:id   (.getRequestId data)
     :data (.toJson data)}))

(defn finish-registration
  "Given a registration challenge and its token response, returns a new
  registered device. If the response is invalid, returns `nil`."
  [data response]
  (try
    (.toJson (.finishRegistration u2f
                                  (RegisterRequestData/fromJson data)
                                  (RegisterResponse/fromJson response)))
    (catch U2fBadInputException _)))

(defn start-authentication
  "Given a user's registered devices, generates a new authentication challenge."
  [app-id devices]
  (let [data (.startAuthentication u2f app-id (parse-devices devices))]
    {:id   (.getRequestId data)
     :data (.toJson data)}))

(defn finish-authentication
  "Given a user's registered devices, an authentication challenge, and its token
  response, returns `true`. If the response is invalid,returns `false`."
  [devices data response]
  (try
    (.finishAuthentication u2f
                           (AuthenticateRequestData/fromJson data)
                           (AuthenticateResponse/fromJson response)
                           (parse-devices devices))
    true
    (catch DeviceCompromisedException _ false)
    (catch U2fBadInputException _ false)))
