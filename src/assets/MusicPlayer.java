package assets;

import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
    //store song details
    private Song currentSong;

    //use JLayer library to create an AdancedPlayer obj which will handle playing the music
    private AdvancedPlayer advancedPlayer;

    //pause boolean flag to indicate wether the player has been paused
    private boolean isPaused;
    public MusicPlayer(){}

    public void loadSong(Song song){
        currentSong=song;
        if(currentSong !=null){
            playCurrentSong();
        }
    }

    public void pauseSong(){
   if(advancedPlayer !=null){
       //update isPaused flag
       isPaused=true;
       //then we want to stop the player
       stopSong();
   }
    }

    public void stopSong(){
        if(advancedPlayer !=null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;

        }
    }

    public void playCurrentSong(){
        try{
            //read mp3 audio data
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);


            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            //start music
            startMusicThread();
        }catch(Exception e){
            e.printStackTrace();

        }

    }

    //a thread that will handle playing the music
    private void startMusicThread(){
   new Thread(new Runnable() {
       @Override
       public void run() {
           try{
             //play music
               advancedPlayer.play();
           }catch(Exception e) {
               e.printStackTrace();
           }
       }
   }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
     //this method gets called in the beginning of the song
        System.out.println("Playback Started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
      //this method gets called when the song is finished
        System.out.println("Playback Finished");
    }
}
