
package cache;

import controllers.UserController;
import model.User;
import utils.Config;

import java.util.ArrayList;

//TODO: Build this cache and use it. FIXED
public class UserCache {

    //List of orders
    private ArrayList<User> users;

    //Time cache should live
    private long ttl;

    // Sets when the cache has been created
    private long created;

    //Constructor for klassen
    public UserCache(){
        this.ttl = Config.getUserTtl();
    }

    public ArrayList<User> getUsers(Boolean forceUpdate) {
        if (forceUpdate
                || ((this.created + this.ttl) >= (System.currentTimeMillis() / 1000L))
                || this.users == null) {

            //Get orders from controller, since we wish to update
            ArrayList<User> users = UserController.getUsers();

            //Set orders for the instance and set created timestamp
            this.users = users;
            this.created = System.currentTimeMillis() / 1000L;
        }

        //Return the documents
        return users;
    }
}
