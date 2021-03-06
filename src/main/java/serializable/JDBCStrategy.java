package serializable;

import interfaces.Playlist;
import interfaces.SerializableStrategy;
import interfaces.Song;

import java.io.IOException;
import java.sql.*;

public class JDBCStrategy implements SerializableStrategy {
    private static Connection con = null;
    private static ResultSet rs = null;
    private static PreparedStatement pstmt = null;
    String insert ="";
    private TableName name = null;

    public enum TableName {
        LIBRARY, PLAYLIST,
    }




    public JDBCStrategy(){
        //Load Driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void delete(TableName table) {
        try {
            switch (table) {
                case LIBRARY:
                    pstmt = con.prepareStatement("DELETE FROM Library");
                    break;
                case PLAYLIST:
                    pstmt = con.prepareStatement("DELETE FROM Playlist");
                    break;
            }
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }


    private void registerDriver() {
        try {
            con = DriverManager.getConnection("jdbc:sqlite:MusicPlayerDB1.db");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Nonnecessary test method
    private void testTable(){
        Statement stmt= null;
        try {
            stmt = con.createStatement();
        } catch (SQLException e) {
            System.out.println("Bad connection.");
        }
        ResultSet rs= null;
        try {
            rs = stmt.executeQuery("select * from Playlist");
        } catch (SQLException e) {
            System.out.println("Problem with execute");
        }
        try {
            if(rs == null) System.out.println("No result set.");
            else{
                while(rs.next())
                    System.out.println(rs.getInt(1)+"  "+rs.getString(2)+"  "+rs.getString(3)+"  "+rs.getString(4)+"  "+rs.getString(5));
            }
        } catch (SQLException e) {
            System.out.println("Problem reading data");
        }

    }

    //Opens the database connection for our library and deletes previous table
    //Save libary
    @Override
    public void openWritableLibrary() throws IOException {
        registerDriver();
        //testTable();
        delete(TableName.LIBRARY);
        insert= "INSERT INTO Library (id, title, interpret, album, path) VALUES (?,?,?,?,?);";
    }

    //Save playlist
    @Override
    public void openWritablePlaylist() throws IOException {
        registerDriver();
        delete(TableName.PLAYLIST);
        insert= "INSERT INTO Playlist (id, title, interpret, album, path) VALUES (?,?,?,?, ?);";
    }

    //Insert songs into DB (Save songs from playlist)
    @Override
    public void writeSong(Song s) throws IOException {
        try {
            pstmt = con.prepareStatement(insert);
            pstmt.setLong(1,  s.getId());
            pstmt.setString(2, s.getTitle());
            pstmt.setString(3, s.getInterpret());
            pstmt.setString(4, s.getAlbum());
            pstmt.setString(5, s.getPath());
            pstmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Get a result set from db containing entire library
    @Override
    public void openReadableLibrary() throws IOException {
        registerDriver();
        try {
            //Create PreparedStatement
            pstmt = con.prepareStatement("SELECT * FROM Library");
            rs = pstmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Get a result set from db containing entire playlist
    @Override
    public void openReadablePlaylist() throws IOException {
        registerDriver();
        try {
            //Create PreparedStatement
            pstmt = con.prepareStatement("SELECT * FROM Playlist");
            rs = pstmt.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /*
   Write songs from the library by calling writeSong for each Song in library
   */
    @Override
    public void writeLibrary(Playlist p) throws IOException {
        for(Song s : p)
            writeSong(s);
        //System.out.println("Test what is in the Database:");
        //testTable();
    }


    //Read a song from Database
    @Override
    public Song readSong() throws IOException, ClassNotFoundException {
        Song song = null;
        try {
            while(rs.next()){
                song = new model.Song(); //use empty constructor
                song.setId(rs.getLong("id"));
                song.setTitle(rs.getString("title"));
                song.setInterpret(rs.getString("interpret"));
                song.setAlbum(rs.getString("album"));
                song.setPath(rs.getString("path"));
                return song;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return song;
    }
    /*
     Read songs into the library by calling readSong until null is returned
     */
    @Override
    public Playlist readLibrary() throws IOException, ClassNotFoundException {
        Playlist list = new model.Playlist();
        Song s = readSong();

        while(s != null){
            list.addSong(s);
            //Read next song
            s = readSong();
        }
        return list;
    }

    @Override
    public void writePlaylist(Playlist p) throws IOException {
        for(Song s : p)
            writeSong(s);
    }

    @Override
    public Playlist readPlaylist() throws IOException, ClassNotFoundException {
        Playlist list = new model.Playlist();
        Song s = readSong();

        while(s != null){
            list.addSong(s);
            //Read next song
            s = readSong();
        }
        return list;
    }

    @Override
    public void closeWritableLibrary() {
        if(rs!= null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println("Result set konnte nicht geschlossen werden beim schliessen von Library.");
            }
        }

        if(pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                System.out.println("Prepared Statement konnte nicht geschlossen werden (nach Library Speicherung).");
            }
        }
        try{
            if(con != null) {
                con.close();
            }

        }catch (SQLException E){
            System.out.println("Connection konnte nicht geschlossen werden.");
        }



    }

    @Override
    public void closeReadableLibrary() {

        if(rs!= null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Result set konnte nicht geschlossen werden beim schliessen von Library.");
            }
        }

        if(pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                System.err.println("PreparedStatement konnte nicht geschlossen werden beim" +
                        "schliessen von Library");
            }
        }

        try{
            if(con != null) {
                con.close();
            }
        }catch (SQLException E){
            System.err.println("Connection konnte nicht geschlossen werden.");
        }

    }


    @Override
    public void closeWritablePlaylist() {

        if(rs!= null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println("Result set konnte nicht geschlossen werden beim schliessen von Library.");
            }
        }

        if(pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                System.out.println("Prepared Statement konnte nicht geschlossen werden (nach PLaylist Speicherung)");
            }
        }

        try{
            if(con != null) {
                con.close();
            }

        }catch (SQLException E){
            System.out.println("Connection konnte nicht geschlossen werden.");
        }



    }

    @Override
    public void closeReadablePlaylist() {

        if(rs!= null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println("Result set konnte nicht geschlossen werden beim schliessen von PLaylist.");
            }
        }
        if(pstmt != null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                System.out.println("PreparedStatement konnte nicht geschlossen werden beim" +
                        "schliessen von Library");
            }
        }
        try{
            if(con != null) {
                con.close();
            }
        }catch (SQLException E){
            System.out.println("Connection konnte nicht geschlossen werden.");
        }

    }

}
