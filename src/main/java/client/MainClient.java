package client;

import java.io.IOException;

public class MainClient {
    public static void main(String[] args){

        Client client = null;
        try {
            client = new Client("localhost", 1234);
        } catch (IOException e) {
            System.out.println("Can not create client!");
            System.exit(-1);
        }

        System.out.println( "Welcome to the MOVIE_LISTER_3000!");
        System.out.println(UserInput.ANSI_BLUE + "If you don't now how to use this app, type 'help'." + UserInput.ANSI_RESET);

        //Создание экземпляра класса, который считывает команды с консоли
        UserInput userInput = new UserInput(client);
        userInput.readCommands();
    }
}
