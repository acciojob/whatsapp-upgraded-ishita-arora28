package com.driver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {
     private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }
    public String createUser(String name,String mobile) throws Exception {
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }
        userMobile.add(mobile);
        User user=new User(name,mobile);
        return "SUCCESS"; 
    }
public Group createGroup(List<User> users) {
       if(users.size()==2){
            Group group=new Group(users.get(1).getName(),2);
            adminMap.put(group,users.get(0));
            groupUserMap.put(group,users);
            groupMessageMap.put(group,new ArrayList<Message>());
            return group;
        }
        this.customGroupCount+=1;
        Group group=new Group(new String("Group "+this.customGroupCount), users.size());
        adminMap.put(group,users.get(0));
         groupUserMap.put(group,users);
         groupMessageMap.put(group,new ArrayList<Message>());
        return group;
}
public int createMessage(String content) {
      this.messageId+=1;
     Message message=new Message(messageId,content);
     return message.getId();
}
public int sendMessage(Message message, User sender, Group group) throws Exception {
     int n=0;
     if(!(groupUserMap.containsKey(group))){
         throw new Exception("Group does not exist");
     }
     else if(!(groupUserMap.get(group).contains(sender))){
         throw new Exception("You are not allowed to send message");
     }
     else {
         List<Message> messages=new ArrayList<>();
         if(groupMessageMap.containsKey(group)) {
             messages = groupMessageMap.get(group);
         }
         messages.add(message);
         groupMessageMap.put(group,messages);
         n=messages.size();
         senderMap.put(message,sender);
     }
     return n;
     
}
public String changeAdmin(User approver, User user, Group group) throws Exception {
     if(!(groupUserMap.containsKey(group))){
          throw new Exception("Group does not exist");
      }
      else if(!(adminMap.get(group).equals(approver))){
          throw new Exception("Approver does not have rights");
      }
      else if(!(groupUserMap.get(group).contains(user))){
          throw new Exception("User is not a participant");
      }
      else{
          adminMap.put(group,user);
          return "SUCCESS";
      }
}
public int removeUser(User user) throws Exception {
    //A user belongs to exactly one group
        //If user is not found in any group, throw "User not found" exception
        //If user is found in a group and it is the admin, throw "Cannot remove admin" exception
        //If user is not the admin, remove the user from the group, remove all its messages from all the databases, and update relevant attributes accordingly.
        //If user is removed successfully, return (the updated number of users in the group + the updated number of messages in group + the updated number of overall messages)
     Group groupUser=new Group();     
        for(Group g:groupUserMap.keySet()){
          List<User> currUsers=groupUserMap.get(g);
          for(User u:currUsers){
               if(u.equals(user)){
                    groupUser=g;
                    break;
               }
          }

     }
     if(groupUser.getName()==null){
          throw new Exception("User not found");
     }
     else if(adminMap.get(groupUser).equals(user)){
           throw new Exception("Cannot remove admin");
     }
     else{
          List<User> users=groupUserMap.get(groupUser);
          users.remove(user);
          List<Message> messages1=new ArrayList<>();
            for(Message message:senderMap.keySet()){
                if(senderMap.get(message).equals(user)){
                    List<Message> messages=groupMessageMap.get(groupUser);
                    messages.remove(message);
                    messages1.add(message);
                }
            }
            for(Message message:messages1){
                senderMap.remove(message);
            }
            String mobile=user.getMobile();
            userMobile.remove(mobile);
            int n=0;
            List<User> usersingroup=groupUserMap.get(groupUser);
            List<Message> messageList=groupMessageMap.get(groupUser);
            n=usersingroup.size()+messageList.size()+senderMap.size();
            return n;

     }

}
     
}
