package client;

import commands.*;
import model.Movie;

/**
 * Енам всех команд приложения имеет одну цель:
 * 1) при вводе пользователем команды довольно просто, без условия под каждую команду, можно сформировать экземпляр введённой пользователем комманды
 */
public enum Commands {
    /**
     * Команда, выводящая в консоль информацию обо всех командах приложения
     */
    HELP("help", "describes all commands"){
        @Override
        public Command formCommand(){
           return new Help();
        }
    },
    /**
     * Команда, выводящая в консоль информацию о коллекции
     */
    INFO("info", "gives data on collections"){
        @Override
        public Command formCommand(){
            return new Info();
        }
    },
    /**
     * Команда, выводящая в консоль все элементы коллекции
     */
    SHOW("show", "demonstrates all collection elements"){
        @Override
        public Command formCommand(){
            return new Show();
        }
    },
    /**
     * Команда, добавляющая в конец коллекции введённый пользователем фильм
     */
    ADD("add", "adds ellement to collection"){
        @Override
        public Command formCommand(){
            Movie movie = UserInput.readMovie();
            if(movie == null) {
                System.out.println("movie is null -> command is null");
                return null;
            }
            return new Add(movie);
        }
    },
    /**
     * Команда, вносящая заданные пользователем изменения в элемент коллекции с заданным id
     */
    UPDATE("update", "changes element with given id"){
        @Override
        Command formCommand(int id){
            Movie movie = UserInput.readMovie();
            if(movie == null) {
                System.out.println("movie is null -> command is null");
                return null;
            }
            return new Update(id, movie);
        }
    },
    /**
     * Команда, удаляющая из коллекции элемент с заданным id
     */
    REMOVE_BY_ID("remove_by_id", "removes element with given id"){
        @Override
        Command formCommand(int id){
            return new RemoveById(id);
        }
    },
    /**
     * Команда, очищающая коллекцию
     */
    CLEAR("clear", "makes the collection empty"){
        @Override
        public Command formCommand(){
            return new Clear();
        }
    },
    /**
     * Команда, выполняющая команды из заданного пользователем файла
     */
    EXECUTE_SCRIPT("execute_script", "executes your script from a given fille"){
        @Override
        Command formCommand(String filleName){

            return new ExecuteScript(filleName);
        }
    },
    /**
     * Команда, завершающая работу приложения
     */
    EXIT("exit", "finishes the programm without saving collection to the fille"){
        @Override
        public Command formCommand(){
            Command exit = new Exit();
            //ТОЛЬКО ТУТ ВЫПОЛНЯЕМ СРАЗУ!
            exit.runCommand();
            System.exit(0);
            return exit;
        }
    },
    /**
     * Команда, удаляющая последний элемент коллекции, при его наличии
     */
    REMOVE_LAST("remove_last", "removes the last element of collection"){
        @Override
        public Command formCommand(){
            return new RemoveLast();
        }
    },
    /**
     * Команда добавляющая элемент в коллекцию, если он меньше минимального элемента коллекции
     */
    ADD_IF_MIN("add_if_min", "adds an element to collection if it is less than current collection minimum"){
        @Override
        public Command formCommand(){
            Movie movie = UserInput.readMovie();
            if(movie == null) return null;
            return new AddIfMin(movie);
        }
    },
    /**
     * Команда, переставляющая элементы коллекции в обратном порядке
     */
    REORDER("reorder", "reverses the order of the collection"){
        @Override
        public Command formCommand(){
            return new Reorder();
        }
    },
    /**
     * Команда, выводящая в консоль количество фильмов, жанр которых, в соответствие с полем genreRating меньше заданного
     */
    COUNT_LES_THAN_GENRE("count_les_than_genre", "tells the ammount of elements whiches genre is lesser than given"){
        @Override
        Command formCommand(String genre){
            return new CountLessThanGenre(genre);
        }
    },
    /**
     * Команда, выводящая в консоль количество фильмов, жанр которых, в соответствие с полем genreRating больше заданного
     */
    COUNT_GREATER_THAN_GENRE("count_greater_than_genre", "tells the amount of elements with gener greater than given"){
        @Override
        Command formCommand(String genre){
            return new CountGreaterThanGenre(genre);
        }
    },
    /**
     * Команда, выводящая в консоль фильмы, жанр которых, в соответствие с полем genreRating больше заданного.
     */
    FILTER_GREATER_THAN_GENRE("filter_greater_than_genre", "outputs the elements with gener greater than given"){
        @Override
        Command formCommand(String genre){
            return new FilterGreaterThanGenre(genre);
        }
    };

    /**
     * Конструктор енама
     * @param commandName имя команды
     * @param description словесное описание команды
     */
    Commands(String commandName, String description) {
        this.commandName = commandName;
        this.description = description;
    }

    /**
     * Переопределяемый командами, на вход которым не подаются данные, метод, реализующий исполнение команд
     *
     * @return
     */
    public Command formCommand(){
        return null;
    }

    /**
     * Переопределяемый командами, на вход которым даётся строковое значение, метод, реализующий исполнение команд
     *
     * @param s строка, с которой должна работать команда
     */
    Command formCommand(String s){
        return null;
    }

    /**
     * Переопределяемый командами, на вход которым даётся целочисленное значение, метод, реализующий исполнение команд
     *
     * @param id целое число, с которым должна работать команда
     */
    Command formCommand(int id){
        return null;
    }

    /**
     * Поле имя команды, используемая в каждой константе енама при помощи конструктора
     */
    final public String commandName;
    /**
     * Поле описание команды, используемое в каждой константе енами при помощи конструктора
     */
    final public String description;

    /**
     * метод возвращающий поле имя команды
     *
     * @return строковое значение - имя команды
     */
    public String getCommandName(){
        return this.commandName;
    }
}
