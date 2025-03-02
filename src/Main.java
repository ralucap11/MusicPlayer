import javax.sound.sampled.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        String filePath = "src\\The Princesses Heart - Density & Time.wav";
        File file = new File(filePath);
        try( Scanner scanner = new Scanner(System.in);
             AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);) {


            Clip clip = AudioSystem.getClip();
            //a clip is like a music or sound player
            clip.open(audioStream);

            String response = "";
            while(!response.equals("q")){
                System.out.println("p= play");
                System.out.println("s= stop");
                System.out.println("r= reset");
                System.out.println("q= quit");
                System.out.print("enter your choice: ");
                response = scanner.next().toLowerCase();

                switch(response){
                    case"p" ->clip.start();
                    case "s" ->clip.stop();
                    case "r" ->clip.setMicrosecondPosition(0);
                    case "q" -> clip.close();
                    default-> System.out.println("wrong choice");
                }
            }

        }
        catch(FileNotFoundException e) {
            System.out.println("could not locate file");
        }
          catch(UnsupportedAudioFileException e){
               System.out.println("audio file is not supported");
            }
        catch(LineUnavailableException e) {
            System.out.println("unable to acces audio resource");
        }
        catch(IOException e){
            System.out.println("error");
        }
        finally{
           System.out.println("bye!");

        }
        }

    }
