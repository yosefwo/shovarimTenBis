import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class GetProperties {
    private static String url;
    private static String browser_properties;
    private static String browser_maven;
    private static String email_properties;
    private static String email_maven;
    private static String months_properties;
    private static String months_maven;
    private static String textVouchers_properties;
    private static String textVouchers_maven;

    public static void setUpProperties() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(System.getProperty("user.dir") + "\\src\\main\\resources\\config.properties");
        Properties prop = new Properties();
        prop.load(fileInputStream);
        url = prop.getProperty("url");
        browser_properties = prop.getProperty("browser");
        browser_maven = System.getProperty("browser");
        email_properties = prop.getProperty("email");
        email_maven = System.getProperty("email");
        months_properties = prop.getProperty("months");
        months_maven = System.getProperty("months");
        textVouchers_properties = prop.getProperty("textVouchers");
        textVouchers_maven = System.getProperty("textVouchers");

    }

    public static String getUrl() {
        return url;
    }

    public static String getBrowser() throws IOException {
        setUpProperties();
        String browser = browser_maven != null ? browser_maven : browser_properties;
        return browser.toLowerCase();
    }
    public static String getEmail() throws IOException {
        setUpProperties();
        return email_maven != null ? email_maven : email_properties;
    }
    public static int getMonths() throws IOException {
        setUpProperties();
        String monthsStr = months_maven != null ? months_maven : months_properties;
        int months;
        try {
            months = Integer.parseInt(monthsStr);
        } catch (Exception e){
            System.out.println("months not number");
            months = 12;
        }
        return months;
    }

    public static String getTextVouchers() {
//        return textVouchers_maven != null ? textVouchers_maven : textVouchers_properties;
        return "שופרסל";
    }

}
