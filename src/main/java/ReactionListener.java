import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReactionListener extends ListenerAdapter {
    public static ReadyListener rl = new ReadyListener();
    public static List<String> Claimed = new ArrayList<>();
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        boolean found = false;
        for (String id : Bot.reactionAvaliableMsgs) {
            String[] stuff = id.split(":");
            if (stuff[0].equalsIgnoreCase(event.getMessageId()) && (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) - Long.parseLong(stuff[1])) <= 15)
                found = true;
        }
        if (found && !event.getMember().getId().equalsIgnoreCase("635330731161157642")) {
            Message msg = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            if (event.getReactionEmote().getName().equalsIgnoreCase("\uD83D\uDC96")) {
                for (String s1 : Claimed) {
                    if (s1.equalsIgnoreCase(event.getMessageId()))
                        return;
                }
                Claimed.add(event.getMessageId());
                event.getChannel().sendMessage(":sparkling_heart: **"+event.getMember().getUser().getName()+"** and **"+msg.getEmbeds().get(0).getTitle()+"** are now married! :sparkling_heart:").queue();
                try {
                    ResultSet rs = rl.GetResultSet("SELECT * FROM mudamaid_clone WHERE userid='"+event.getMember().getUser().getId()+"' and serverid='"+event.getGuild().getId()+"';");
                    while (rs.next()) {
                        JSONObject jo = ((JSONObject) new JSONParser().parse(rs.getString(4)));
                        jo.put(msg.getEmbeds().get(0).getTitle(), true);
                        PreparedStatement pts = Bot.con.prepareStatement("UPDATE mudamaid_clone SET claims = '"+jo.toJSONString()+"' WHERE userid='"+event.getMember().getUser().getId()+"' and serverid='"+event.getGuild().getId()+"';");
                        pts.execute();
                    }
                } catch (Exception e) {

                }
                return;
            }
            List<String> Pics = rl.PictureGet(msg.getEmbeds().get(0).getTitle());
            Pattern pt = Pattern.compile("\\d+ / \\d+");
            Matcher m = pt.matcher(msg.getEmbeds().get(0).getFooter().getText());
            String[] s = {};
            int currentPicture = 0;
            while(m.find()) {
                s = m.group().split(" / ");
                break;
            }
            for (String s1 : s) {
            }
            currentPicture = Integer.parseInt(s[0])-1;
            try {
                if (event.getReactionEmote().getId().equalsIgnoreCase("635731678526177290")) {
                    currentPicture = currentPicture - 1;
                    if ((currentPicture) < 0)
                        currentPicture = Pics.size() - 1;
                    msg.getEmbeds().get(0).toData().getObject("image").put("url", Pics.get(currentPicture));
                    String txt = msg.getEmbeds().get(0).toData().getObject("footer").getString("text");
                    txt = txt.replace(s[0] + " / " + s[1], (currentPicture + 1) + " / " + Pics.size());
                    msg.getEmbeds().get(0).toData().getObject("footer").put("text", txt);
                    msg.editMessage(msg.getEmbeds().get(0)).queue();
                } else if (event.getReactionEmote().getId().equalsIgnoreCase("635731638323511316")) {
                    currentPicture = currentPicture + 1;
                    if (currentPicture + 1 > Pics.size())
                        currentPicture = 0;
                    msg.getEmbeds().get(0).toData().getObject("image").put("url", Pics.get(currentPicture));
                    String txt = msg.getEmbeds().get(0).toData().getObject("footer").getString("text");
                    txt = txt.replace(s[0] + " / " + s[1], (currentPicture + 1) + " / " + Pics.size());
                    msg.getEmbeds().get(0).toData().getObject("footer").put("text", txt);
                    msg.editMessage(msg.getEmbeds().get(0)).queue();
                }
            } catch (Exception e)  {}
        }
    }
}