package gclproject.onesong;

final public class Config {
    // Check out http://go.microsoft.com/fwlink/p/?LinkId=193157 to get your own client id
    public static final String CLIENT_ID = "0000000000000000"; // Update to your Microsoft Client ID here

    // Available options to determine security level of access
    public static final String[] SCOPES = {
            "wl.basic",
            "wl.signin",
            "wl.skydrive",
            "wl.contacts_skydrive"
    };

    private Config() {
        throw new AssertionError("Unable to create Config object.");
    }
}
