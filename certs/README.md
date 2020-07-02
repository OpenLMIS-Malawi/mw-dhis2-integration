# Update of SSL certificates for Kuunika in DHIS2 integration service. 

### To update those certificates you need keytool. 

To add certificates files firstly you need to get them from server. You can download them directly from kuunika using your web browser `security` tab or using `openssl`.  

Then just add them to the keystore using this command (where cacerts is path to ./certs/cacerts in this repository).

```keytool -import -trustcacerts -file cert_name.crt -alias cert_name.crt -keystore cacerts ```

You have to redeploy instance to see those changes.