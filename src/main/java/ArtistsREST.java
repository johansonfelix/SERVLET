import impl.Artists;
import org.json.JSONObject;
import pojo.Artist;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.http.HTTPException;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.stream.Collectors;

@WebServlet(name = "Artists", urlPatterns = "/artist")
public class ArtistsREST extends HttpServlet {

    Artists artistList;

    @Override
    public void init(){
        artistList = new Artists();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject jsonObject = new JSONObject(jsonString);
        String nickname = jsonObject.getString("nickname");
        String firstname = jsonObject.getString("firstName");
        String lastname = jsonObject.getString("lastName");
        String bio = jsonObject.getString("bio");
        try (PrintWriter out = response.getWriter()) {

            if((nickname!=null && firstname!=null && lastname!=null)||bio!=null){

                if ((nickname.length()!=0 && firstname.length()!=0 && lastname.length()!=0)) {
                    artistList.addArtist(new Artist(firstname, lastname, nickname, bio));
                }
                else{
                    out.println("Invalid Parameters.");
                    throw new HTTPException (HttpServletResponse.SC_BAD_REQUEST);
                }
            }
            else{
                out.println("Invalid Parameters.");
                throw new HTTPException (HttpServletResponse.SC_BAD_REQUEST);
            }

            out.println("Artist was added successfully.");
            out.flush();
        }
        catch (IOException e){
            throw new HTTPException (HttpServletResponse.SC_BAD_REQUEST);
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
                    throw new HTTPException (HttpServletResponse.SC_BAD_REQUEST);
                }
            }
            if(getType.equals("artist")){
                String nickname = request.getParameter("nickname");
                if(nickname!=null){
                    try (PrintWriter out = response.getWriter()) {

                        if(artistList.artistExists(nickname))
                            out.println(artistList.getArtist(nickname).toString());
                        else
                            out.println("Artist does not exist!");
                        out.flush();
                    }
                    catch (IOException e){
                        throw new HTTPException (HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
                else{
                    throw new HTTPException (HttpServletResponse.SC_BAD_REQUEST);
                }
            }
            else{
                throw new HTTPException (HttpServletResponse.SC_BAD_REQUEST);
            }
        }
        else{
            PrintWriter out = response.getWriter();
            out.println("Service Active.");
        }
    }
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String jsonString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject jsonObject = new JSONObject(jsonString);
        String nickname = jsonObject.getString("nickname");
        Artist artistToUpdate = null;
        PrintWriter out = response.getWriter();
        if(artistList.artistExists(nickname)) {
            artistToUpdate = artistList.getArtist(nickname);
            String attribute = jsonObject.getString("attribute");
            if(attribute.equals("all")){
                artistToUpdate.setFirstName(jsonObject.getString("newFirstName"));
                artistToUpdate.setLastName(jsonObject.getString("newLastName"));
                artistToUpdate.setBio(jsonObject.getString("newBio"));
            }
            else {
                String newValue = jsonObject.getString("newValue");
                switch (attribute) {
                    case "firstName": {
                        artistToUpdate.setFirstName(newValue);
                        break;
                    }
                    case "lastName": {
                        artistToUpdate.setLastName(newValue);
                        break;
                    }

                    case "bio": {
                        artistToUpdate.setBio(newValue);
                        break;
                    }
                }
            }
            out.println("Artist Updated. New values:");
            out.println(artistToUpdate.toString());
        }
        else{
            out.println("Could not update artist. Reason: That artist does not exist.");
        }
        System.out.println(artistToUpdate.toString());
        out.flush();
    }
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nickname = request.getParameter("nickname");
        PrintWriter out = response.getWriter();
        if(artistList.artistExists(nickname)){
            artistList.deleteArtist(nickname);
            out.println("Artist deleted successfully.");
        }
        else{
            out.println("That artist does not exist.");
        }
        out.flush();
    }
}
