import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import javax.security.auth.login.LoginException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;

public class Bot implements EventListener {
    public static JDA bot;
    public static Connection con;
    public static List<String> reactionAvaliableMsgs = new ArrayList<String>();
    public static void main(String[] args)
            throws LoginException
    {
        JDA jda = new JDABuilder(args[0])
                .addEventListeners(new ReadyListener())
                .addEventListeners(new ReactionListener()).build();

        bot = jda;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/obfuscated_ram?useSSL=false", "toor", "root");
            System.out.printf("[SYS MSG] %s - %s\n", "SQL Connection has been Established.", "Connected to server: localhost:3306/obfuscated_ram!");
        } catch (Exception e) {e.printStackTrace();}
    }

    public void onEvent(GenericEvent event)
    {
        if(event instanceof ReadyEvent)
            System.out.println("API is ready!");
    }

}