import com.sun.javafx.collections.MappingChange;
import impl.Artists;



import pojo.Artist;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "Artists", urlPatterns = "/artists")
public class ArtistsREST extends HttpServlet {

    Artists artistList;

    @Override
    public void init(){
        artistList = new Artists();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String nickname = request.getParameter("nickname");
        String firstname = request.getParameter("firstname");
        String lastname = request.getParameter("lastname");
        String bio = request.getParameter("bio");

        try (PrintWriter out = response.getWriter()) {

            if((nickname!=null && firstname!=null && lastname!=null)||bio!=null){

                if ((nickname.length()!=0 && firstname.length()!=0 && lastname.length()!=0)) {
                    artistList.addArtist(new Artist(firstname, lastname, nickname, bio));
                }
                else{
                    //error handling
                }
            }
            else{
                //some error handling
                throw new HTTPException (HttpServletResponse.SC_BAD_REQUEST);
            }

            //Success message
            out.println(artistList.getArtist(nickname));
            out.flush();

        }
        catch (IOException e){
            //some error handling
            e.getMessage();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String getType = request.getParameter("get");

        if(getType!=null){
            if (getType.equals("all")){
                try (PrintWriter out = response.getWriter()) {
                    out.println(artistList.toString());
                    out.flush();
                }

                catch (IOException e){
                    //some error handling
                    e.getMessage();
                }
            }
            if(getType.equals("artist")){
                String nickname = request.getParameter("nickname");
                if(nickname!=null){
                    try (PrintWriter out = response.getWriter()) {

                        if(artistList.getArtist(nickname)!=null)
                            out.println(artistList.getArtist(nickname).toString());
                        else
                            out.println("Artist does not exist!");

                        out.flush();
                    }

                    catch (IOException e){
                        //some error handling
                        e.getMessage();
                    }

                }
                else{
                    //error handling
                }
            }
            else{
                //error handling
            }
        }
        else{
            //error handling
        }




    }


    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        Map<String, String>  map = getParameterMap(request);

        if(map!=null) {

            String update = map.get("update");

            switch (update) {
                case "firstname": {
                    String firstname = map.get("firstname");
                    String nickname = map.get("nickname");
                    artistList.getArtist(nickname).setFirstName(firstname);
                    break;

                }
                case "lastname": {
                    String lastname = map.get("lastname");
                    String nickname = map.get("nickname");
                    artistList.getArtist(nickname).setLastName(lastname);
                    break;
                }

                case "bio": {
                    String bio = map.get("bio");
                    String nickname = map.get("bio");
                    artistList.getArtist(nickname).setBio(bio);
                    break;
                }
                case "all": {
                    String firstname = map.get("firstname");
                    String lastname = map.get("lastname");
                    String bio = map.get("bio");
                    String nickname = map.get("nickname");
                    artistList.getArtist(nickname).setFirstName(firstname);
                    artistList.getArtist(nickname).setLastName(lastname);
                    artistList.getArtist(nickname).setBio(bio);
                }

            }
        }




        PrintWriter out = response.getWriter();
        out.println("Artist Updated");
        System.out.println(map.entrySet());
        out.println(map.entrySet());
        out.flush();
    }
    // /nickname=##&firstname=##&lastname=##&bio=##
    public static Map<String, String> getParameterMap(HttpServletRequest request){

        Map<String, String> parameters;
        BufferedReader br;

        try{

            br = new BufferedReader(new InputStreamReader(request.getInputStream()));

            String data = br.readLine();
            System.out.println(data);

            String[] args = data.split("&");
            System.out.println(args[0]);
          parameters = new HashMap<>();

            for(String s : args){

                String[] strings = s.split("=");
                System.out.println("here 1");
                parameters.put(strings[0], strings[1]);

            }

            return parameters;

                    }

        catch (IOException e){
            System.out.println("ERROR!");
        }

        return null;
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, String>  map = getParameterMap(request);

        if(map!=null) {

            String nickname = map.get("nickname");
            artistList.deleteArtist(nickname);

        }


    }
}
