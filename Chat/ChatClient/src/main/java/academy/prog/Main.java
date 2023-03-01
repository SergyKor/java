package academy.prog;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		try {
			System.out.println("Enter your login: ");
			String login = scanner.nextLine();
	
			Thread th = new Thread(new GetThread(login));
			th.setDaemon(true);
			th.start();

            System.out.println("Enter your message: ");
			while (true) {
				String text = scanner.nextLine();
				if (text.isEmpty()) break;

				// users
				// @test Hello

				if(text.equals("/users")){
					new GetThread(login).getUsersList();
					continue;
				}

                String to = "ALL";
                String textMassage = "";
                if(text.startsWith("@")){
                    char [] textChar = text.toCharArray();
                    for( int i = 0; i < textChar.length; i++){
                        to = new String(Arrays.copyOfRange(textChar,1,i));
                        textMassage = new String(Arrays.copyOfRange(textChar, i+1,textChar.length));
                        break;
                    }
                }
                else {
                    textMassage = text;
                }

				Message m = new Message(login, text, to);
				int res = m.send(Utils.getURL() + "/add");

				if (res != 200) { // 200 OK
					System.out.println("HTTP error occurred: " + res);
					return;
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			scanner.close();
		}
	}
}
