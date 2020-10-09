/*
 * ProxyChat, a Velocity chat solution
 * Copyright (C) 2020 James Lyne
 *
 * Based on BungeeChat2 (https://github.com/AuraDevelopmentTeam/BungeeChat2)
 * Copyright (C) 2020 Aura Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.co.notnull.ProxyChat.api;

import uk.co.notnull.ProxyChat.api.enums.ChannelType;
import uk.co.notnull.ProxyChat.api.placeholder.ProxyChatContext;
import uk.co.notnull.ProxyChat.api.placeholder.InvalidContextError;
import uk.co.notnull.ProxyChat.api.utils.ProxyChatInstaceHolder;

import java.io.File;

/** This is the base Interface for the ProxyChatApi. The central methods will be found here */
public interface ProxyChatApi {
  public static final String ID = "proxychat";
  public static final String NAME = "Proxy Chat";
  public static final String DESCRIPTION = "Proxy Chat Plugin";
  public static final String URL = "https://github.com/JLyne/ProxyChat";
  public static final String AUTHOR_JIM = "Jim";
  public static final String AUTHOR_BRAINSTONE = "BrainStone";
  public static final String AUTHOR_SHAWN = "shawn_ian";
  public static final String[] AUTHORS = new String[] {AUTHOR_JIM, AUTHOR_BRAINSTONE, AUTHOR_SHAWN};
  public static final String[] CONTRIBUTORS =
      new String[] {
        "AwesomestGamer",
        "Brianetta",
        "CryLegend",
        "gb2233",
        "Hodel1",
        "Luck",
        "MineTech13",
        "n0dai"
      };
  public static final String[] TRANSLATORS =
      new String[] {
        "DardBrinza",
        "Fantasenf",
        "fjeddy",
        "Garixer",
        "gb2233",
        "Itaquito",
        "marzenie",
        "Maxime_74",
        "povsister"
      };
  public static final String[] DONATORS = new String[] {"Breantique", "NickT"};
  public static final double CONFIG_VERSION = 11.7;

  /**
   * Method to retrieve the instance of the API
   *
   * @return The ProxyChatApi instance
   */
  public static ProxyChatApi getInstance() {
    return ProxyChatInstaceHolder.getInstance();
  }

  /**
   * Retrieves (and creates if necessary) the config folder.
   *
   * @return The existing config folder
   */
  public File getConfigFolder();

  /**
   * Send a private message. The context contains the sender, the target and the message!
   *
   * @param context Containing sender, target and message.
   * @throws InvalidContextError Throws and {@link InvalidContextError} if either a sender, target
   *     or message is missing in this context.
   */
  public void sendPrivateMessage(ProxyChatContext context) throws InvalidContextError;

  /**
   * Sends a message from the sender in the context to the specified channel. The message has to be
   * in the context.
   *
   * @param context Containing sender and message.
   * @param channel What channel to send the message in.
   * @throws InvalidContextError Throws and {@link InvalidContextError} if either a sender or
   *     message is missing in this context.
   */
  public void sendChannelMessage(ProxyChatContext context, ChannelType channel)
      throws InvalidContextError;

  /**
   * The same as {@link ProxyChatApi#sendChannelMessage(ProxyChatContext, ChannelType)}. But uses
   * the channel the sender is currently in.
   *
   * @param context Containing sender and message.
   * @throws InvalidContextError Throws and {@link InvalidContextError} if either a sender or
   *     message is missing in this context.
   */
  public default void sendChannelMessage(ProxyChatContext context) throws InvalidContextError {
    if (context.hasSender()) {
      sendChannelMessage(context, context.getSender().get().getChannelType());
    } else {
      sendChannelMessage(context, ChannelType.LOCAL);
    }
  }
}
