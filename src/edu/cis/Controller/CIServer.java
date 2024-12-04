/*
 * File: CIServer.java
 * ------------------------------
 * When it is finished, this program will implement a basic
 * ecommerce network management server.  Remember to update this comment!
 */

package edu.cis.Controller;

import acm.program.*;
import edu.cis.Model.*;
import edu.cis.Utils.SimpleServer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class CIServer extends ConsoleProgram
        implements SimpleServerListener
{

    /* The internet port to listen to requests on */
    private static final int PORT = 8000;

    /* The server object. All you need to do is start it */
    private SimpleServer server = new SimpleServer(this, PORT);

    private ArrayList<CISUser> users = new ArrayList<>();
    private Menu menu = new Menu();

    /**
     * Starts the server running so that when a program sends
     * a request to this server, the method requestMade is
     * called.
     */
    public void run()
    {
        println("Starting server on port " + PORT);
        server.start();
    }

    /**
     * When a request is sent to this server, this method is
     * called. It must return a String.
     *
     * @param request a Request object built by SimpleServer from an
     *                incoming network request by the client
     */
    public String requestMade(Request request)
    {
        String cmd = request.getCommand();
        println(request.toString());

        // your code here.
        if (request.getCommand().equals(CISConstants.CREATE_USER))
        {
            return createUser(request);
        }
        if(request.getCommand().equals(CISConstants.DELETE_ORDER)){
            return deleteOrder(request);
        }
        if(request.getCommand().equals(CISConstants.ADD_MENU_ITEM)){
            return addMenuItem(request);
        }
        if(request.getCommand().equals(CISConstants.PLACE_ORDER)){
            return placeOrder(request);
        }
        if (request.getCommand().equals(CISConstants.PING))
        {
            final String PING_MSG = "Hello, internet";

            //println is used instead of System.out.println to print to the server GUI
            println("   => " + PING_MSG);
            return PING_MSG;
        }
        if(request.getCommand().equals(CISConstants.GET_ITEM)){
            return getItem(request);
        }
        if(request.getCommand().equals(CISConstants.GET_USER)){
            return getUser(request);
        }
        if(request.getCommand().equals(CISConstants.GET_ORDER)){
            return getOrder(request);
        }

        return "Error: Unknown command " + cmd + ".";
    }

    public String createUser(Request req){
        String userId = req.getParam(CISConstants.USER_ID_PARAM);
        String name = req.getParam(CISConstants.USER_NAME_PARAM);
        String yearLevel = req.getParam(CISConstants.YEAR_LEVEL_PARAM);

        if (userId == null || name == null || yearLevel == null) {
            return CISConstants.PARAM_MISSING_ERR;
        }

        CISUser newUser = new CISUser(userId, name, yearLevel);
        try {
            users.add(newUser );
        } catch (Exception e) {
            println("Error adding user: " + e.getMessage());
            return "Error: Unable to add user.";
        }
        return CISConstants.SUCCESS;

    }

    public String addMenuItem(Request req){
        String itemName = req.getParam(CISConstants.ITEM_NAME_PARAM);
        String desc = req.getParam(CISConstants.DESC_PARAM);
        double price = Double.parseDouble(req.getParam(CISConstants.PRICE_PARAM));
        String itemId = req.getParam(CISConstants.ITEM_ID_PARAM);
        String itemType = req.getParam(CISConstants.ITEM_TYPE_PARAM);

        if (itemId == null || itemType == null || itemName == null||req.getParam(CISConstants.PRICE_PARAM)==null||desc==null) {
            return CISConstants.PARAM_MISSING_ERR;
        }
        MenuItem newMenuItem = new MenuItem(itemName,desc,price,itemId,itemType);
        menu.addEatriumItem(newMenuItem);
        return CISConstants.SUCCESS;

    }

    public String placeOrder(Request req) {
        String itemId = req.getParam(CISConstants.ITEM_ID_PARAM);
        String orderType = req.getParam(CISConstants.ORDER_TYPE_PARAM);
        String orderId = req.getParam(CISConstants.ORDER_ID_PARAM);
        String userId = req.getParam(CISConstants.USER_ID_PARAM);
        CISUser user = null;

        if (itemId == null || orderType == null || orderId == null||userId==null) {
            return CISConstants.PARAM_MISSING_ERR;
        }
        ArrayList<MenuItem> eatriumItems = menu.getEatriumItems();
        if(eatriumItems.isEmpty()){
            return CISConstants.EMPTY_MENU_ERR;
        }
        double price = 0;
        MenuItem item = null;
        for(int i=0;i<eatriumItems.size();i++){
            if(eatriumItems.get(i).getId().equals(itemId)){
                item=eatriumItems.get(i);
                price = item.getPrice();
            }
        }
        if(item==null){
            return CISConstants.INVALID_MENU_ITEM_ERR;
        }
        Order newOrder;
        try {
            newOrder = new Order(itemId, orderType, orderId);
        } catch (Exception e) {
            return CISConstants.EMPTY_MENU_ERR;
        }
        for(int i=0;i<users.size();i++){
            if(users.get(i).getUserID().equals(userId)){
                user=users.get(i);
            }
        }
        if(user==null){
            return CISConstants.USER_INVALID_ERR;
        }
        CISUser userTemp;
        for(int i=0;i<users.size();i++) {
            userTemp=users.get(i);
            for (int j = 0; j < userTemp.getOrders().size(); j++) {
                if (userTemp.getOrders().get(j).getOrderID().equals(orderId)) {
                    return CISConstants.DUP_ORDER_ERR;
                }
            }
        }
        if(user.getMoney()-price<0){
            return CISConstants.USER_BROKE_ERR;
        }
        user.addOrder(newOrder,price);
        int newAmount = item.getAmountAvailable();
        println(newAmount);
        newAmount--;
        println(newAmount);
        item.setAmountAvailable(newAmount);
        println(user.getOrders());
        println(newOrder);
        return CISConstants.SUCCESS;
    }

    public String deleteOrder(Request req){
        String userId = req.getParam(CISConstants.USER_ID_PARAM);
        String orderId = req.getParam(CISConstants.ORDER_ID_PARAM);
        CISUser user = null;
        for (CISUser cisUser : users) {
            if (cisUser.getUserID().equals(userId)) {
                user = cisUser;
            }
        }
        if(user==null){
            return CISConstants.USER_INVALID_ERR;
        }
        for(int i=0;i<user.getOrders().size();i++){
            if(user.getOrders().get(i).getOrderID().equals(orderId)){
                user.removeOrder(i);
                return CISConstants.SUCCESS;
            }
        }
        return CISConstants.ORDER_INVALID_ERR;

    }

    public String getOrder(Request req){
        String userId = req.getParam(CISConstants.USER_ID_PARAM);
        String orderId = req.getParam(CISConstants.ORDER_ID_PARAM);
        CISUser user = null;
        for(int i=0;i<users.size();i++){
            if(users.get(i).getUserID().equals(userId)){
                user=users.get(i);
            }
        }
        if(user==null){
            return CISConstants.USER_INVALID_ERR;
        }
        for(int i=0;i<user.getOrders().size();i++){
            if(user.getOrders().get(i).getOrderID().equals(orderId)){
                return user.getOrders().get(i).toString();
            }
        }
        return CISConstants.ORDER_INVALID_ERR;

    }

    public String getUser(Request req){
        String userId = req.getParam(CISConstants.USER_ID_PARAM);
        for(int i=0;i<users.size();i++){
            if(users.get(i).getUserID().equals(userId)){
                return users.get(i).toString();
            }
        }
        return CISConstants.USER_INVALID_ERR;

    }

    public String getItem(Request req){
        String itemId = req.getParam(CISConstants.ITEM_ID_PARAM);
        ArrayList<MenuItem> eatriumItems = menu.getEatriumItems();
        if(eatriumItems.isEmpty()){
            return CISConstants.EMPTY_MENU_ERR;
        }
        for(int i=0;i<eatriumItems.size();i++){
            if(eatriumItems.get(i).getId().equals(itemId)){
                return eatriumItems.get(i).toString();
            }
        }
        return CISConstants.INVALID_MENU_ITEM_ERR;



    }

    public String getCart(Request req){
        return "createUser{userid=”007”, name=”Ashley”, year=”12”}";

    }


    public static void main(String[] args)
    {
        CIServer f = new CIServer();
        f.start(args);
    }

}
