package serializable;

import interfaces.Playlist;
import interfaces.SerializableStrategy;
import interfaces.Song;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;

public class XMLStrategy implements SerializableStrategy {

    private FileOutputStream fos = null;
    private XMLEncoder encoder = null; // = oos

    private FileInputStream fis = null;
    private XMLDecoder decoder = null; // = ois

    private Playlist library;

    @Override
    //öffne Streams zur Serialisierung der library
    public void openWritableLibrary() throws IOException {
        fos = new FileOutputStream("xmlLibraray.xml");
        encoder = new XMLEncoder(fos);
    }

    @Override
    //öffne Streams zur Deserialisierung der library
    public void openReadableLibrary() throws IOException {
        fis = new FileInputStream("xmlLibraray.xml");
        decoder = new XMLDecoder(fis);
    }

    @Override
    //öffne Streams zur Serialisierung der playlist
    public void openWritablePlaylist() throws IOException {
        fos = new FileOutputStream("xmlPlaylist.xml");
        encoder = new XMLEncoder(fos);
    }

    @Override
    //öffne Streams zur Deserialisierung der playlist
    public void openReadablePlaylist() throws IOException {
        fis = new FileInputStream("xmlPlaylist.xml");
        decoder = new XMLDecoder(fis);
    }

    /** ******************************************************************************************************* **/

    @Override
    // serialisiere ein Object (Song)
    public void writeSong(Song s) throws IOException {
        encoder.writeObject(s);
        System.out.println("writing XML Song: " + s.getTitle());
    }

    @Override
    // deserialisiere ein Object (Song)
    public Song readSong() throws IOException, ClassNotFoundException {
        Song s;
        s = (Song) decoder.readObject();
        if (s != null) { return s; }
        else { return null; }

    }

    @Override
    // serialisierte library (ArrayList<Song>)
    // iteriere dazu über die library (ArrayList<Song>)
    public void writeLibrary(Playlist p) throws IOException {
        for (Song s : p) { writeSong(s); }
    }

    @Override
    // deserialisierte library (ArrayList<Song>)
    // iteriere dazu über die library (ArrayList<Song>)
    // gibt ganze playlist zurück
    /*  - erstelle neue Playlist
        - lese Song     (exception möglich)
        - füge gelesenen Song der Playlist hinzu
        - wenn keine Songs mehr gelesen werden können gebe Playlist zurück */
    public Playlist readLibrary() throws IOException, ClassNotFoundException {
        model.Playlist p = new model.Playlist();
        Song so = readSong(); // erstelle Objekt (einzelner ausgelesener Song)
        while (so != null) {  // alternative Abfrage: fis.available()!= 0 (?)
            try {
                p.add(so); //füge ausgelenes Objekt (Song) der library hinzu
                so = readSong(); //setzte so auf das nächste auszulesene Objekt (Song) -> setzt Schleife fort
            } catch (IOException | ArrayIndexOutOfBoundsException e) { /* e.printStackTrace(); */ break; }
        }
        library = p;
        return p;
    }

    @Override
    /** schreibe Song(Objekt) (writeSong() ) in playlist **/
    public void writePlaylist(Playlist p) throws IOException {
        for (Song s : p) { writeSong(s); }
    }

    @Override
    //
    //analog
    public Playlist readPlaylist() throws IOException, ClassNotFoundException {
        model.Playlist p = new model.Playlist();
        Song so = readSong(); // erstelle Objekt (einzelner ausgelesener Song)
        while (so != null) {  // alternative Abfrage: fis.available()!= 0 (?)
            try {
                so.getId();
                /*finde Song aus library mit dem gleichen Index wie aus ausgelesene Objekt (Song)
                  wenn Index gleich: füge Song der Playlist p hinzu
                     -> SELBE Songs werden in library + playlist bearbeitet ( controller.edit() ) */
                for (Song s : library) {
                    if(s.getId() == so.getId()) p.add(s);
                }
                so = readSong(); //setzte so auf das nächste auszulesene Objekt (Song) -> setzt Schleife fort
            } catch (IOException | ArrayIndexOutOfBoundsException e) { /* e.printStackTrace(); */ break; }
        }
        return p;
    }

    /** ******************************************************************************************************** **/

    @Override
    public void closeWritableLibrary() {
        try {
            if (encoder != null) encoder.close();
            if (fos != null) fos.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void closeReadableLibrary() {
        try {
            if (decoder != null) decoder.close();
            if (fis != null) fis.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void closeWritablePlaylist() {
        try {
            if (encoder != null) encoder.close();
            if (fos != null) fos.close();
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void closeReadablePlaylist() {
        try {
            if (decoder != null) decoder.close();
            if (fis != null) fis.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}