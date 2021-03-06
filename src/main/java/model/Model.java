package model;
import exception.IDOverFlowException;
import model.IDGenerator;


import org.apache.openjpa.lib.util.UUIDGenerator;

import java.io.File;
import java.rmi.RemoteException;

public class Model {

    private Playlist library = new Playlist();
    private Playlist playlist= new Playlist();
    private IDGenerator idGenerator;

    public Model() {
        //essentially a load data method
        File f= new File(System.getProperty("user.dir") + "/Songs");
        File[] paths= f.listFiles();
        idGenerator = new IDGenerator();


        if (paths != null) {
            try {
                for (File path : paths) {
                    if (path.toString().endsWith(".mp3")) {

                        library.addSong(new Song( path.toString(), path.getName(), "", "", idGenerator.getNextID()  ));

                    }
                }


            } catch (RemoteException e) {
                System.out.println("Entfernter Rechner nicht zu erreichen");
            } catch (IDOverFlowException e){
                System.out.println("Keine ID's mehr vorhanden.");
            }
        }
    }


    public Playlist getLibrary(){ return library; }

    public Playlist getPlaylist() {
        return playlist;
    }

    public void setLibrary(interfaces.Playlist library){ this.library = (Playlist) library; }

    public void setPlaylist(interfaces.Playlist playlist){ this.playlist = (Playlist) playlist; }


}


