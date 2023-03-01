package academy.prog;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class GetThread implements Runnable {
    private final Gson gson;
    private int n; // /get?from=n
    private Set<String> usersList = new HashSet<>();
    private String login;

    public GetThread(String login) {
        gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        this.login = login;
    }


    @Override
    public void run() { // WebSockets - ДЗ!!!
        try {
            while ( ! Thread.interrupted()) {
                URL url = new URL(Utils.getURL() + "/get?from=" + n);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();

                InputStream is = http.getInputStream();
                try {
                    byte[] buf = responseBodyToArray(is);
                    String strBuf = new String(buf, StandardCharsets.UTF_8);

                    JsonMessages list = gson.fromJson(strBuf, JsonMessages.class);
                    if (list != null) {
                        for (Message m : list.getList()) {
                            if(m.getFrom().equals(login)){
                                System.out.println(m);
                                n++;
                            } else if (m.getTo().equals("All")) {
                                System.out.println(m);
                                n++;
                            }
                            else {
                                n++;
                            }

                        }
                    }
                } finally {
                    is.close();
                }

                Thread.sleep(500);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private byte[] responseBodyToArray(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buf = new byte[10240];
        int r;

        do {
            r = is.read(buf);
            if (r > 0) bos.write(buf, 0, r);
        } while (r != -1);

        return bos.toByteArray();
    }
    public void getUsersList() throws IOException{
        int firstMessage = 0;
        URL url = new URL(Utils.getURL() + "/get?from=" + firstMessage);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


        try (InputStream inputStream = httpURLConnection.getInputStream()) {
            byte [] buffer = responseBodyToArray(inputStream);
            String strBuffer = new String(buffer, StandardCharsets.UTF_8);

            JsonMessages list = gson.fromJson(strBuffer, JsonMessages.class);
            if (list != null){
                for (Message m : list.getList()){
                    usersList.add(m.getFrom());
                }
            }
            else {
                usersList.add(login);
            }

        }finally {
            System.out.println("Users");
            for (String user : usersList){
                System.out.println("@" + user);
            }
        }
    }
}
