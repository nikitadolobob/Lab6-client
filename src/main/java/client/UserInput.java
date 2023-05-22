package client;


import commands.CommandSystem;
import model.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

/**
 * Класс, экземпляры которого позволяют работать с командами, введёнными пользователем как с консоли, так и при помощи файла
 */
public class UserInput {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_GREEN = "\u001B[32m";


    private static String filleName;
    private final Client client;

    /**
     * Конструктор экземпляра класса применимый в случае, если пользователь хочет считать команды со своего файла
     *
     * @param filleName имя заданного пользователем файла-скрипта
     */
    public UserInput(String filleName, Client client){
        this.client = client;
        this.filleName = filleName;
    }

    /**
     * Сканер, для считывания команд пользователя и с консоли, и через файл
     */
    public static Scanner sc;

    /**
     * Конструктор экземпляра красса применимый в случае если пользователь хочет вводить команды через консоль
     */
    public UserInput(Client client){
        this.client = client;
    }

    /**
     * Метод, удаляющий из строки лишние пробелы и формирующий из неё строку, в которой между каждым словом ровно один пробел
     * @param unformedRemover Поступающая на вход строка
     * @return Сформированная строка, без лишних пробелов
     */
    private static String removeFormer(String unformedRemover){
        if(unformedRemover.trim().equals("")){
            return "";
        }
        else {
            unformedRemover = unformedRemover.trim();
            String[] former = unformedRemover.split(" ");
            Vector<String> vector = new Vector<>();
            for (String i : former) {
                if (!i.equals(" ")) {
                    vector.add(i);
                }
            }
            String a = vector.get(0);
            vector.remove(vector.get(0));
            for (String i : vector) {
                a += " " + i;
            }
            return a;
        }
    }

    /**
     * Метод реализующий распознавание и выполнение команд, предварительно выяснив, поступают они через консоль или из файла
     */
    public void readCommands(){
        if(filleName == null) sc = new Scanner(System.in);
        else {
            try {
                sc = new Scanner(new File(filleName));
            } catch (FileNotFoundException e) {
                messageNewLineWriter("No such file");
                return;
            }
        }

        while(true){
            if(filleName == null) System.out.print("&~ ");
            boolean isDumb = true;
            String s;
            try{
                s = sc.nextLine();
            }
            catch(NoSuchElementException e) {
                break;
            }
            String a = removeFormer(s);
            if(a.equals("")){
                continue;
            }
            String[] vector = a.split(" ");

            for(Commands i : Commands.values()){
                if(i.getCommandName().equals(vector[0])){
                    isDumb = false;
                    if(vector[0].equals("execute_script") || vector[0].equals("update") || vector[0].equals("remove_by_id") || vector[0].equals("count_les_than_genre") || vector[0].equals("count_greater_than_genre") || vector[0].equals("filter_greater_than_genre")){
                        if(vector.length < 2){
                            messageNewLineWriter("This command should have an argument");
                            continue;
                        }
                        if(vector[0].equals("execute_script") || vector[0].equals("count_les_than_genre") || vector[0].equals("count_greater_than_genre") || vector[0].equals("filter_greater_than_genre")){
                            if(vector[0].equals("execute_script")){

                                if(filleName != null && filleName.equals(vector[1])){
                                    System.out.println("Recursive script call. Command cancelled");
                                    break;
                                }
                                else if(CommandSystem.execute_cnt > 0){
                                    if(CommandSystem.execute_cnt % 10 == 0){
                                        System.out.println("You have a lot of scripts inside of other scripts. Do you wanna to continue? Enter something if YES, stay line blank otherwise");
                                        Scanner resp_sc = new Scanner(System.in);
                                        String response = resp_sc.nextLine();
                                        if(response.isEmpty()) break;
                                    }
                                }
                            }

                            if(!vector[0].equals("execute_script")){
                                client.sendCommand(i.formCommand(vector[1]));
                            }
                            else {
                                File f = new File(vector[1]);
                                if(!f.exists()){
                                    UserInput.messageNewLineWriter("Such filleName does not exist. Make sure you entered ABSOLUTE PATH to the file");
                                    continue;
                                }
                                UserInput.messageNewLineWriter("Start reading script " + vector[1]);
                                CommandSystem.execute_cnt++;
                                UserInput userInput = new UserInput(vector[1], client);
                                userInput.readCommands();
                                CommandSystem.execute_cnt--;
                                UserInput.messageNewLineWriter("Script executing command finished. You may get to entering data via console");
                            }
                            //i.runCommand(vector[1]);
                        }
                        else{
                            try{
                                client.sendCommand(i.formCommand(Integer.parseInt(vector[1])));
                                //i.runCommand(Integer.parseInt(vector[1]));
                            }catch (NumberFormatException e){
                                messageNewLineWriter("Command argument must be integer");
                            }
                        }
                    }
                    else{
                        client.sendCommand(i.formCommand());
                        //i.runCommand();
                    }
                }
            }
            if(isDumb){
                messageNewLineWriter("You entered: " + s);
                messageNewLineWriter("There is no such command");
            }
            UserInput.messageNewLineWriter(ANSI_BLUE + "Enter your next command: " + ANSI_RESET);

        }
        if(filleName != null){
            filleName = null;
            sc = new Scanner(System.in);
        }
    }

    /**
     * Метод выводящий пользователю сообщения на новой строке, помогающие ему ориентироваться в своих дальнейших действиях в приложении
     *
     * @param s выводимое сообщение
     */
    public static void messageNewLineWriter(String s){
        if(filleName == null){
            System.out.println(s);
        }
    }

    /**
     * Метод выводящий пользователю сообщения без перехода на новую строку, помогающие ему ориентироваться в своих дальнейших действиях в приложении
     *
     * @param s выводимое сообщение
     */
    public static void messageThisLineWriter(String s){
        if(filleName == null){
            System.out.print(s);
        }
    }

    /**
     * Метод, считыващий введённые пользователем данные заполняющий ими поля экземпляра класса Movie
     *
     * @return возвращает заполненный экземпляр класса Movie
     */
    public static Movie readMovie(){

        Movie movie = new Movie();

        try {
            String name = "";
            while (name.trim().equals("")) {
                messageNewLineWriter("Enter movie name. Movie name can't be empty.");
                messageThisLineWriter("&~ ");
                name = sc.nextLine();
                if(!name.trim().equals("")) messageNewLineWriter("Your movie name is " + name);
            }
            movie.setName(name);

            Integer x = null;
            while (x == null) {
                messageNewLineWriter("Enter Integer x coordinate");
                messageThisLineWriter("&~ ");
                try {
                    String s = sc.nextLine();
                    x = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    messageNewLineWriter("int number required ");
                }
            }
            Float y = null;
            while (y == null || y > 208) {
                if (y == null) {
                    messageNewLineWriter("Enter Float y coordinate that is not greater than 208");
                    messageThisLineWriter("&~");
                } else {
                    messageNewLineWriter("Number must be greater 208");
                    messageThisLineWriter("&~ ");
                }
                try {
                    String s = sc.nextLine();
                    y = Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    y = null;
                    messageNewLineWriter("Number is required");
                }
            }
            Coordinates coordinates = new Coordinates(x, y);

            movie.setCoordinates(coordinates);
            Integer oscarCount = null;
            while (oscarCount == null || oscarCount < 1) {
                if (oscarCount == null) {
                    messageNewLineWriter("Enter Integer amount of oscar that is greater than 0");
                    messageThisLineWriter("&~ ");

                } else {
                    messageNewLineWriter("number must be above 0");
                    messageThisLineWriter("&~ ");
                }
                try {
                    String s = sc.nextLine();
                    oscarCount = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    oscarCount = null;
                    messageNewLineWriter("Int number required");
                }
            }
            movie.setOscarsCount(oscarCount);

            String genre = "";
            while (genre.equals("")) {
                messageNewLineWriter("Choose one of the given genres and enter it (in caps)");
                for (MovieGenre gener : MovieGenre.values()) {
                    messageNewLineWriter(gener.name());
                }
                messageThisLineWriter("&~");
                genre = sc.nextLine();
                boolean isAnAss = true;
                for (MovieGenre gener : MovieGenre.values()) {
                    if (gener.name().equals(genre)) {
                        movie.setGenre(gener);
                        isAnAss = false;
                    }
                }
                if (isAnAss) {
                    genre = "";
                    messageNewLineWriter("Incorrect genre. Type one of the options from the list precisely");
                }
            }

            String rating = null;
            boolean isAnAss = true;
            while (isAnAss && rating == null) {
                messageNewLineWriter("Choose one of the given ratings and enter it (in caps)");
                for (MpaaRating mpaaRating : MpaaRating.values()) {
                    messageNewLineWriter(mpaaRating.name());
                }
                messageThisLineWriter("&~");
                rating = sc.nextLine();
                for (MpaaRating mpaaRating : MpaaRating.values()) {
                    if (mpaaRating.name().equals(rating)) {
                        movie.setMpaaRating(mpaaRating);
                        isAnAss = false;
                    }
                }
                if (isAnAss && !rating.equals("")) {
                    rating = null;
                    messageNewLineWriter("Incorrect rating. Type one of the options from the list precisely");
                }
            }

            Person director = new Person();
            String directorName = "";
            while (directorName.trim().equals("")) {
                messageNewLineWriter("Enter director name. Director name can't be empty.");
                messageThisLineWriter("&~");
                directorName = sc.nextLine();
            }
            director.setName(directorName);

            Float weight = null;
            messageNewLineWriter("Enter Float weight that is greater 0");
            messageThisLineWriter("&~");
            while (weight == null || weight <= 0) {
                if(weight != null){
                    messageNewLineWriter("Number greater than 0 required");
                }
                try {
                    String s = sc.nextLine();
                    if (s.equals("")) break;
                    weight = Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    weight = null;
                    messageNewLineWriter("Number is required");
                }
            }
            director.setWeight(weight);

            String eColour = null;
            boolean isIncorrect = true;
            while (isIncorrect && eColour == null){
                messageNewLineWriter("Choose one of the given eye colors for the director (in caps) or enter empty line to make it null");
                for (model.colorEyes.Color color : model.colorEyes.Color.values()) {
                    messageNewLineWriter(color.name());
                }
                messageThisLineWriter("&~");
                eColour = sc.nextLine();
                for (model.colorEyes.Color color : model.colorEyes.Color.values()) {
                    if (color.name().equals(eColour)) {
                        isIncorrect = false;
                        director.setEyeColor(color);
                    }
                }
                if(isIncorrect && !eColour.equals("")){
                    messageNewLineWriter("No such eye color. Try again or enter empty line to set it null");
                    eColour = null;
                }
            }

            String hColour = null;
            isIncorrect = true;
            while (isIncorrect){
                messageNewLineWriter("Choose one of the given hair colors for the director (in caps)");
                for (model.colorHair.Color color : model.colorHair.Color.values()) {
                    messageNewLineWriter(color.name());
                }
                messageThisLineWriter("&~");
                hColour = sc.nextLine();
                for (model.colorHair.Color color : model.colorHair.Color.values()) {
                    if (color.name().equals(hColour)) {
                        director.setHairColor(color);
                        isIncorrect = false;
                    }
                }
                if(isIncorrect){
                    messageNewLineWriter("No such hair color!");
                }
            }


            String countryName;
            isIncorrect = true;
            while (isIncorrect){
                messageNewLineWriter("Choose one of the given origin countries for the director (in caps)");
                for (Country country : Country.values()) {
                    messageNewLineWriter(country.name());
                }
                messageThisLineWriter("&~");
                countryName = sc.nextLine();
                for (Country country : Country.values()) {
                    if (country.name().equals(countryName)) {
                        director.setNationality(country);
                        isIncorrect = false;
                    }
                }
                if(isIncorrect){
                    messageNewLineWriter("No such country!");
                }
            }

            Double xl = null;
            while (xl == null) {
                messageNewLineWriter("Enter double x coordinate of directors location");
                messageThisLineWriter("&~");
                try {
                    String s = sc.nextLine();
                    xl = Double.parseDouble(s);
                } catch (NumberFormatException e) {
                    messageNewLineWriter("You should enter double value");
                }
            }


            Integer yl = null;
            while (yl == null) {
                messageNewLineWriter("Enter int y coordinate of directors location");
                messageThisLineWriter("&~");
                try {
                    String s = sc.nextLine();
                    yl = Integer.parseInt(s);
                } catch (NumberFormatException e) {
                    messageNewLineWriter("You should enter integer value");
                }
            }

            Float zl = null;
            while (zl == null) {
                messageNewLineWriter("Enter float z coordinate of directors location");
                messageThisLineWriter("&~");
                try {
                    String s = sc.nextLine();
                    zl = Float.parseFloat(s);
                } catch (NumberFormatException e) {
                    messageNewLineWriter("You should enter float value");
                }
            }

            Location location = new Location(xl, yl, zl);
            director.setLocation(location);

            movie.setDirector(director);
            return movie;
        }
        catch(NoSuchElementException e){
            if(filleName != null) {
                //System.out.println("error");
                messageNewLineWriter("Some things from the script were not correct or were unfinished. No incorrect/unfinished data was taken into account");
            }
            return null;
        }
    }
}
