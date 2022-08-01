package com.github.wnebyte.engine.components;

import java.util.*;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import com.github.wnebyte.engine.core.ecs.Component;
import com.github.wnebyte.engine.animation.AnimationState;
import com.github.wnebyte.engine.animation.Frame;

public class StateMachine extends Component {

    static class StateTrigger {

        String state;

        String trigger;

        StateTrigger() {}

        StateTrigger(String state, String trigger) {
            this.state = state;
            this.trigger = trigger;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null) return false;
            if (o == this) return true;
            if (!(o instanceof StateTrigger)) return false;
            StateTrigger st = (StateTrigger)o;
            return Objects.equals(st.state, this.state) &&
                    Objects.equals(st.trigger, this.trigger);
        }

        @Override
        public int hashCode() {
            int result = 77;
            return 2 * result +
                    Objects.hashCode(state) +
                    Objects.hashCode(trigger);
        }
    }

    public final Map<StateTrigger, String> stateTransfers = new HashMap<>();

    private final List<AnimationState> states = new ArrayList<>();

    private transient AnimationState currentState = null;

    private String defaultStateTitle = "";

    public StateMachine() {}

    @Override
    public void start() {
        for (AnimationState state : states) {
            if (state.title.equals(defaultStateTitle)) {
                currentState = state;
                break;
            }
        }
    }

    @Override
    public void update(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
            if (spr != null) {
                spr.setSprite(currentState.getCurrentSprite());
            }
        }
    }

    @Override
    public void editorUpdate(float dt) {
        if (currentState != null) {
            currentState.update(dt);
            SpriteRenderer spr = gameObject.getComponent(SpriteRenderer.class);
            if (spr != null) {
                spr.setSprite(currentState.getCurrentSprite());
            }
        }
    }

    @Override
    public void imGui() {
        int index = 0;
        for (AnimationState state : states) {
            ImString title = new ImString(state.title);
            ImGui.inputText("State", title);
            state.title = title.get();

            ImBoolean doesLoop = new ImBoolean(state.doesLoop);
            ImGui.checkbox("Does Loop", doesLoop);
            state.doesLoop = doesLoop.get();
            for (Frame frame : state.frames) {
                float[] tmp = new float[1];
                tmp[0] = frame.frameTime;
                ImGui.dragFloat("Frame(" + index + ") Time", tmp, 0.01f);
                frame.frameTime = tmp[0];
                index++;
            }
        }
    }

    public void trigger(String trigger) {
        for (StateTrigger st : stateTransfers.keySet()) {
            if (st.state.equals(currentState.title) && st.trigger.equals(trigger)) {
                if (stateTransfers.get(st) != null) {
                    int newStateIndex = stateIndexOf(stateTransfers.get(st));
                    if (newStateIndex > -1) {
                        currentState = states.get(newStateIndex);
                    }
                }
                return;
            }
        }

        System.out.printf("(Debug): Unable to find trigger: '%s'%n", trigger);
    }

    private int stateIndexOf(String stateTitle) {
        int index = 0;
        for (AnimationState state : states) {
            if (state.title.equals(stateTitle)) {
                return index;
            }
            index++;
        }

        return -1;
    }

    public void addStateTrigger(String from, String to, String onTrigger) {
        stateTransfers.put(new StateTrigger(from, onTrigger), to);
    }

    public void addState(AnimationState state) {
        states.add(state);
    }

    public void setDefaultState(String animationTitle) {
        for (AnimationState state : states) {
            if (state.title.equals(animationTitle)) {
                defaultStateTitle = state.title;
                if (currentState == null) {
                    currentState = state;
                    return;
                }
            }
        }

        System.out.printf("(Debug): Unable to find state: '%s'%n", animationTitle);
    }

    public void refreshTextures() {
        for (AnimationState state : states) {
            state.refreshTextures();
        }
    }
}
