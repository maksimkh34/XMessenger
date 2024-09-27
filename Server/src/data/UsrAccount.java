package data;

public class UsrAccount extends CanDecrypt {
    public String usrId;
    public String secret;

    @Override
    public String GetHmacKey() {
        return usrId + secret;
    }
}
