package me.servercaster.servercasterextension;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.servercaster.main.ServerCaster;
import me.servercaster.main.event.CastListener;
import me.servercaster.main.event.PreCastEvent;
import me.servercaster.main.event.PreCastPlayerEvent;
import net.amoebaman.util.Reflection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Patrick Beuks (killje) and Floris Huizinga (Flexo013)
 */
public class ServerUtils extends JavaPlugin implements CastListener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ServerCaster.addMessageListener(this, this);
    }

    @Override
    public void castHandler(PreCastEvent e) {
        ArrayList<String> messages = e.getMessages();
        ArrayList<String> newMessages = new ArrayList<>();
        for (String string : messages) {
            Random rand = new Random();
            Player[] onlinePlayers = this.getServer().getOnlinePlayers();
            string = string.toLowerCase().replace(("%RDMPLAYER%").toLowerCase(), onlinePlayers[rand.nextInt(onlinePlayers.length)].getName());
            string = string.toLowerCase().replace(("%SLOTS%").toLowerCase(), this.getServer().getMaxPlayers() + "");
            string = string.toLowerCase().replace(("%PLAYERS%").toLowerCase(), onlinePlayers.length + "");
            if (string.toLowerCase().contains(("%ONLINEPLAYERS%").toLowerCase())) {
                String players = "";
                for (Player player : onlinePlayers) {
                    players += player.getDisplayName() + ", ";
                }
                players = players.substring(0, players.length() - 2);
                string = string.replace("%ONLINEPLAYERS%", players);
            }
            List<String> staff = getConfig().getStringList("Staff");
            if (string.toLowerCase().contains(("%LISTALLSTAFF%").toLowerCase())) {
                String allStaff = "";
                for (String staffName : staff) {
                    allStaff += staffName + ", ";
                }
                allStaff = allStaff.substring(0, allStaff.length() - 2);
                string = string.replace("%LISTALLSTAFF%", allStaff);
            }
            if (string.toLowerCase().contains(("%ONLINESTAFF%").toLowerCase())) {
                String onlineStaff = "";
                for (String staffName : staff) {
                    if (getServer().getPlayer(staffName).isOnline()) {
                        onlineStaff += staffName + ", ";
                    }
                }
                onlineStaff = onlineStaff.substring(0, onlineStaff.length() - 2);
                string = string.replace("%ONLINESTAFF%", onlineStaff);
            }
            newMessages.add(string);
        }
        e.setMessages(newMessages);
    }

    @Override
    public void castPlayerHandler(PreCastPlayerEvent e) {
        ArrayList<String> messages = e.getMessages();
        ArrayList<String> newMessages = new ArrayList<>();
        for (String string : messages) {
            string = string.toLowerCase().replace(("%PLAYER%").toLowerCase(), e.getPlayer().getName());
            string = string.toLowerCase().replace(("%PING%").toLowerCase(), getPing(e.getPlayer()) + "");
            newMessages.add(string);
        }
        e.setMessages(newMessages);
    }

    private int getPing(Player p) {
        Class<?> cp = Reflection.getOBCClass("entity.CraftPlayer").cast(p).getClass();
        int returnvalue = -1;
        Object ep;
        try {
            ep = Reflection.getMethod(cp, "getHandler").invoke(null);
            returnvalue = Reflection.getField(ep.getClass(), "ping").getInt(ep);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(ServerUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return returnvalue;
    }
}
