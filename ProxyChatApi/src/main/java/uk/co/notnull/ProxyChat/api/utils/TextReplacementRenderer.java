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

package uk.co.notnull.ProxyChat.api.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.kyori.adventure.text.*;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.renderer.ComponentRenderer;
import net.kyori.adventure.util.IntFunction2;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A renderer performing a replacement on every {@link TextComponent} element of a component tree.
 */
public final class TextReplacementRenderer implements ComponentRenderer<TextReplacementRenderer.State> {
  public static final TextReplacementRenderer INSTANCE = new TextReplacementRenderer();

  private TextReplacementRenderer() {
  }

  @Override
  public @NonNull Component render(final @NonNull Component component, final @NonNull State state) {
    if(!state.running) return component;
    state.replaceCount = 0;

    List<Component> children = null;
    Component modified = component;
    // replace the component itself
    if(component instanceof TextComponent) {
      final String content = ((TextComponent) component).content();
      final Matcher matcher = state.pattern.matcher(content);
      int replacedUntil = 0; // last index handled
      while(matcher.find()) {
        final PatternReplacementResult result = state.continuer.apply(++state.matchCount, state.replaceCount);
        if(result == PatternReplacementResult.CONTINUE) {
          // ignore this replacement
          continue;
        } else if(result == PatternReplacementResult.STOP) {
          // end replacement
          state.running = false;
          break;
        }

        if(matcher.start() == 0) {
          // if we're a full match, modify the component directly
          if(matcher.end() == content.length()) {
            final ComponentLike replacement = state.replacement.apply(matcher, Component.text().content(matcher.group())
              .style(component.style()));

            modified = replacement == null ? Component.empty() : replacement.asComponent();
          } else {
            // otherwise, work on a child of the root node
            modified = Component.text("", component.style());
            final ComponentLike child = state.replacement.apply(matcher, Component.text().content(matcher.group()));
            if(child != null) {
              children = listOrNew(children, component.children().size() + 1);
              children.add(child.asComponent());
            }
          }
        } else {
          children = listOrNew(children, component.children().size() + 2);
          if(state.replaceCount == 0) {
            // truncate parent to content before match
            modified = ((TextComponent) component).content(content.substring(0, matcher.start()));
          } else if(replacedUntil < matcher.start()) {
            children.add(Component.text(content.substring(replacedUntil, matcher.start())));
          }
          final ComponentLike builder = state.replacement.apply(matcher, Component.text().content(matcher.group()));
          if(builder != null) {
            children.add(builder.asComponent());
          }
        }
        state.replaceCount++;
        replacedUntil = matcher.end();
      }
      if(replacedUntil < content.length()) {
        // append trailing content
        if(replacedUntil > 0) {
          children = listOrNew(children, component.children().size());
          children.add(Component.text(content.substring(replacedUntil)));
        }
        // otherwise, we haven't modified the component, so nothing to change
      }
    } else if(modified instanceof TranslatableComponent) { // get TranslatableComponent with() args
      final List<Component> args = ((TranslatableComponent) modified).args();
      List<Component> newArgs = null;
      for(int i = 0; i < args.size(); i++) {
        final Component original = args.get(i);
        final Component replaced = this.render(original, state);
        if(replaced != component) {
          if(newArgs == null) {
            newArgs = new ArrayList<>(args.size());
            if(i > 0) {
              newArgs.addAll(args.subList(0, i));
            }
          }
        }
        if(newArgs != null) {
          newArgs.add(replaced);
        }
      }
      if(newArgs != null) {
        modified = ((TranslatableComponent) modified).args(newArgs);
      }
    }
    // Only visit children if we're running
    if(state.running) {
      // hover event
      final HoverEvent<?> event = modified.style().hoverEvent();
      if(event != null) {
        final HoverEvent<?> rendered = event.withRenderedValue(this, state);
        if(event != rendered) {
          modified = modified.style(s -> s.hoverEvent(rendered));
        }
      }
      // Children
      final List<Component> oldChildren = component.children();
      boolean first = true;
      for(int i = 0, size = oldChildren.size(); i < size; i++) {
        final Component child = oldChildren.get(i);
        final Component replaced = this.render(child, state);
        if(replaced != child) {
          children = listOrNew(children, size);
          if(first) {
            children.addAll(oldChildren.subList(0, i));
          }
          first = false;
        }
        if(children != null) {
          children.add(replaced);
        }
      }
    } else {
      // we're not visiting children, re-add original children if necessary
      if(children != null) {
        children.addAll(component.children());
      }
    }

    // Update the modified component with new children
    if(children != null) {
      return modified.children(children);
    }
    return modified;
  }

  private static <T> @NonNull List<T> listOrNew(final @Nullable List<T> init, final int size) {
    return init == null ? new ArrayList<>(size) : init;
  }

  public static final class State {
    final Pattern pattern;
    final BiFunction<MatchResult, TextComponent.Builder, @Nullable ComponentLike> replacement;
    final IntFunction2<PatternReplacementResult> continuer;
    boolean running = true;
    int matchCount = 0;
    int replaceCount = 0;

    public State(
            final @NonNull Pattern pattern,
            final @NonNull BiFunction<MatchResult, TextComponent.Builder, @Nullable ComponentLike> replacement,
            final @NonNull IntFunction2<PatternReplacementResult> continuer) {
      this.pattern = pattern;
      this.replacement = replacement;
      this.continuer = continuer;
    }
  }
}

