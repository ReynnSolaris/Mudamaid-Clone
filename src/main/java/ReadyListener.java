import javafx.scene.paint.Color;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.requests.Route;
import org.apache.commons.compress.utils.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ReadyListener extends ListenerAdapter {
    public static ResultSet GetResultSet(String SQL) {
        ResultSet RS = null;
        try {
            Statement stmt = Bot.con.createStatement();
            RS = stmt.executeQuery(SQL);
        } catch(SQLException e) {}
        return RS;
    }
    public JSONObject JSONData(String name) {
        JSONObject jsonObject = new JSONObject();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("animeBitches.json")) {
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject)jsonParser.parse(
                    new InputStreamReader(inputStream, "UTF-8"));
            Object a = jsonObject.get(name);
            String nameD = "";
            for (Object o : jsonObject.keySet()) {
                try {
                    if(o.toString().length() >= name.length()) {
                        if (o.toString().substring(0, name.length()).equalsIgnoreCase(name)) {
                            a = jsonObject.get(o.toString());
                            nameD = o.toString();
                        }
                    }
                } catch(Exception e) {e.printStackTrace();}
            }
            if (a != null) {
                jsonObject = (JSONObject)jsonParser.parse(a.toString());
            }
            jsonObject.put("CLAIMED_NAME", nameD);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
    public boolean IsClaimed(String name, String serverId) {
        ResultSet dab = GetResultSet("SELECT * FROM mudamaid_clone WHERE serverid = '"+serverId+"';");
        try {
            while(dab.next()) {
                JSONObject cls = ((JSONObject) new JSONParser().parse(dab.getString(4)));
                    for (Object o : cls.keySet()) {
                        if(o.toString().equalsIgnoreCase(name))
                            return true;
                    }
            }
        } catch(Exception e) {}
        return false;
    }
    public List<String> GetClaimedInfo(String name, String serverId) {
        List<String> dt = new ArrayList<>();
        ResultSet dab = GetResultSet("SELECT * FROM mudamaid_clone WHERE serverid = '"+serverId+"';");
        try {
            while(dab.next()) {
                JSONObject cls = ((JSONObject) new JSONParser().parse(dab.getString(4)));
                for (Object o : cls.keySet()) {
                    if (o.toString().equalsIgnoreCase(name)) {
                        User dab2 = Bot.bot.getUserById(dab.getString(3));
                        dt.add(dab2.getName());
                        dt.add(dab2.getAvatarUrl());
                    }
                }
            }
        } catch(Exception e) {}
        return dt;
    }
    public List<String> DataGet(String name) {
        JSONObject Data = JSONData(name);
        List<String> Info = new ArrayList<>();
        String FemaleIcon = "<:female:635730404921311292>";
        String gender = "";
        String KakeraAmount = Data.get("Kakera_Worth").toString();
        if(Data.get("Gender").toString().equalsIgnoreCase("female")) {
            gender = FemaleIcon;
        } else {
        }
        Info.add(gender);
        Info.add(KakeraAmount);
        Info.add(Data.get("Claim_Number").toString());
        Info.add(Data.get("Like_Number").toString());
        Info.add(Data.get("Nickname").toString());
        Info.add(Data.get("CLAIMED_NAME").toString());
        Info.add(Data.get("Anime").toString());
        return Info;
    }
    public List<String> PictureGet(String name) {
        JSONObject Data = JSONData(name);
        JSONParser jp = new JSONParser();
        JSONObject picJSON;
        List<String> Pictures = new ArrayList<>();
        try {
            picJSON = (JSONObject)jp.parse(Data.get("Pictures").toString());
            for (Object pictures : (picJSON).keySet()) {
                Pictures.add(picJSON.get(pictures.toString()).toString());
            }
        } catch (Exception e) {}
        return Pictures;
    }
    public EmbedBuilder createPage(String name, User author, String sId) {
        List<String> Data = DataGet(name);
        List<String> Pictures = PictureGet(name);
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(Data.get(5))
                .setDescription(Data.get(6)+" "+Data.get(0)+"\n"
                        + "*Animanga roulette* · **"+Data.get(1)+"**<:kakera:635728178693799936>\n"
                        + "Claims: #"+Data.get(2)+"\n"
                        + "Likes: #"+Data.get(3)+"\n"
                        + Data.get(4))
                .setColor(new java.awt.Color(103, 12, 8))
                .setImage(Pictures.get(0));
        if(IsClaimed(Data.get(5), sId)) {
            List<String> claimedInfo = GetClaimedInfo(Data.get(5), sId);
            eb.setFooter("Belongs to " + claimedInfo.get(0) + " ~~ " + 1 + " / " + Pictures.size(), claimedInfo.get(1));
        } else {
            eb.setFooter("1 / " + Pictures.size());
        }
        return eb;
    }
    public EmbedBuilder createClaim(String name) {
        List<String> Data = DataGet(name);
        List<String> Pictures = PictureGet(name);
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle(Data.get(5))
                .setDescription(Data.get(6)+"")
                .setColor(new java.awt.Color(103, 12, 8))
                .setImage(Pictures.get(0));
        return eb;
    }
    public String getRandomAnimeBitch(String sId) {
        JSONObject jsonObject = new JSONObject();
        List<String> AmimeBitches = new ArrayList<>();
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("animeBitches.json")) {
            JSONParser jsonParser = new JSONParser();
            jsonObject = (JSONObject) jsonParser.parse(
                    new InputStreamReader(inputStream, "UTF-8"));
            for (Object o : jsonObject.keySet()) {
                if (!IsClaimed(o.toString(), sId)) {
                    AmimeBitches.add(o.toString());
                }
            }
            Random dab = new Random(System.currentTimeMillis());
            return AmimeBitches.get(dab.nextInt(AmimeBitches.size()));
        } catch(Exception e) {}

        return "";
    }
    public void onMessageReceived(MessageReceivedEvent event)
    {
       // if (event.getAuthor().isBot()) return;
        // We don't want to respond to other bot accounts, including ourself
        Message message = event.getMessage();
        String content = message.getContentRaw();
        // getContentRaw() is an atomic getter
        /* getContentDisplay() is a lazy getter which modifies the content for e.g. console view (strip discord formatting)
        if (event.getGuild().getId().equalsIgnoreCase("190237475313156096")) {
            if (content.toLowerCase().startsWith("wished by")) {
                if (event.getAuthor().getId().equalsIgnoreCase("548984223592218634") || event.getAuthor().getId().equalsIgnoreCase("635330731161157642")) {
                    try {
                        if (message.getReactions().size() != 0) {
                            for (MessageReaction reaction : message.getReactions()) {
                                message.addReaction(reaction.getReactionEmote().getEmote()).queue();
                            }
                            return;
                        }
                        message.addReaction("\uD83D\uDC96").queue();
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }
                }
            }
        }
        */
        if (content.startsWith("!testwish")) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("A'Luo");
            eb.setDescription("Sorcerer Maiden");
            eb.setImage("https://media.discordapp.net/attachments/472313197836107780/606995249784684565/hTt7wET.png");
            event.getChannel().sendMessage("Wished by, " + event.getAuthor().getAsMention()).complete();
            event.getChannel().sendMessage(eb.build()).complete().addReaction("✔").queue();
        } else if (content.startsWith("!testclaimed")) {
            EmbedBuilder page = createPage("Ram", event.getAuthor(), event.getGuild().getId());
            Message cl = event.getChannel().sendMessage(page.build()).complete();
            Bot.reactionAvaliableMsgs.add(cl.getId() + ":" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            cl.addReaction(Bot.bot.getGuildById("591335900642934864").getEmotesByName("left", true).get(0)).complete();
            cl.addReaction(Bot.bot.getGuildById("591335900642934864").getEmotesByName("right", true).get(0)).queue();
        } else if (content.startsWith("!im")) {
            String[] args = content.substring(4).split("\\$");
            try {
                EmbedBuilder page = createPage(args[0].toLowerCase(), event.getAuthor(), event.getGuild().getId());
                Message cl = event.getChannel().sendMessage(page.build()).complete();
                Bot.reactionAvaliableMsgs.add(cl.getId() + ":" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                cl.addReaction(Bot.bot.getGuildById("591335900642934864").getEmotesByName("left", true).get(0)).complete();
                cl.addReaction(Bot.bot.getGuildById("591335900642934864").getEmotesByName("right", true).get(0)).queue();
            } catch(NullPointerException e) {
                event.getChannel().sendMessage("Sorry, but I couldn't find what you were looking for!").queue();
            }
        } else if (content.startsWith("!w")) {
            String a = getRandomAnimeBitch(event.getGuild().getId());
            if (a.equalsIgnoreCase("")) {
                event.getChannel().sendMessage("Seems everything has been claimed already!").queue();
                return;
            }
            EmbedBuilder page = createClaim(a);
            Message cl = event.getChannel().sendMessage(page.build()).complete();
            Bot.reactionAvaliableMsgs.add(cl.getId() + ":" + TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
            cl.addReaction("\uD83D\uDC96").complete();
        } else if (content.startsWith("!mm")) {
            EmbedBuilder eb = new EmbedBuilder()
                    .setTitle(event.getAuthor().getName()+"'s Harem")
                    .setColor(new java.awt.Color(103, 12, 8));
            ResultSet dab = GetResultSet("SELECT * FROM mudamaid_clone WHERE userid= '"+event.getAuthor().getId()+"' and serverid = '"+event.getGuild().getId()+"';");
            String first = "";
            try {
                while (dab.next()) {
                    JSONObject jo = ((JSONObject) new JSONParser().parse(dab.getString(4)));
                    for (Object o : jo.keySet()) {
                        if (first.equalsIgnoreCase(""))
                            first = o.toString();
                        eb.appendDescription(o.toString()+"\n");
                    }
                }
                eb.setThumbnail(PictureGet(first).get(0));
                event.getChannel().sendMessage(eb.build()).queue();
            } catch( Exception e) {}
        }
    }
}