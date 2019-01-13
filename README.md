# LocalProxy
As the name suggests, the solution provides local proxy service on the device. This with the main objective of carrying out the authentication process with a proxy server and in this way allow access to the Internet in the installed applications. Initially it is very similar to applications like UCIntlm or UCIProxy, that is, it allows access to the Internet through the proxy server of a certain institution. Its difference lies in the idea that led to its development: it must operate in as many Cuban institutional networks as possible.
For the development of the solution, a study of the authentication schemes most used in the proxy servers was carried out, a somewhat complicated study that yielded achievable results. In addition, similar applications were studied in detail: ProxyDroid, SandroProxy, Drony and UCIntlm, from which important functionalities were extracted that were implemented taking into account the level of usability for the end user. For its implementation, the UCIProxy and UCIntlm applications were reused at the abstraction and object level, several functionalities were added and important changes were introduced in the local proxy service.

## Main functionalities:
### Version 1.0
    1. Support for Basic, Digest, NTLMv1, NTLMv2 and NLTM2 Session authentication schemes.
    2. Management of connection profiles.
    3. Application of firewall rules for outgoing requests from the device.
    4. Display of navigation traces generated in the device.
    5. Automatic configuration of the Wi-Fi proxy for Android versions below Marshmallow (this due to changes made to the system and the functioning of the API to come from that version).
    6. Verification of the user's credentials directly with the institution's proxy server.

### Version 1.1
    1. HTTP header management.
    2. Fixed bug with notifications in Android 8.0.