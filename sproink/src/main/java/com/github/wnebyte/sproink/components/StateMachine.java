package com.github.wnebyte.sproink.components;

import java.util.*;
import imgui.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import com.github.wnebyte.sproink.core.Component;

public class StateMachine extends Component {

    private static class StateTrigger {

        private final String state;

        private final String trigger;

        private StateTrigger(String state, String trigger) {
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

    private final Map<StateTrigger, String> stateTransfers = new HashMap<>();

    private final List<AnimationState> states = new ArrayList<>();

    private String defaultStateTitle = "";

    private transient AnimationState currentState = null;

    public StateMachine() {}

    @Override
    public void start() {
        for (AnimationState state : states) {
            if (state.getTitle().equals(defaultStateTitle)) {
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
    public void refresh() {
        for (AnimationState state : states) {
            state.refresh();
        }
    }

    @Override
    public void imGui() {
        int index = 0;
        for (AnimationState state : states) {
            ImString title = new ImString(state.getTitle());
            ImGui.inputText("State", title);
            state.setTitle(title.get());

            ImBoolean doesLoop = new ImBoolean(state.doesLoop());
            ImGui.checkbox("Does Loop", doesLoop);
            state.setLoops(doesLoop.get());
            for (Frame frame : state.getFrames()) {
                float[] tmp = new float[1];
                tmp[0] = frame.getFrameTime();
                ImGui.dragFloat("Frame(" + index + ") Time", tmp, 0.01f);
                frame.setFrameTime(tmp[0]);
                index++;
            }
        }
    }

    public void trigger(String trigger) {
        for (StateTrigger st : stateTransfers.keySet()) {
            if (st.state.equals(currentState.getTitle()) && st.trigger.equals(trigger)) {
                if (stateTransfers.get(st) != null) {
                    int newStateIndex = stateIndexOf(stateTransfers.get(st));
                    if (newStateIndex > -1) {
                        currentState = states.get(newStateIndex);
                    }
                }
                return;
            }
        }
    }

    private int stateIndexOf(String stateTitle) {
        int index = 0;
        for (AnimationState state : states) {
            if (state.getTitle().equals(stateTitle)) {
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
            if (state.getTitle().equals(animationTitle)) {
                defaultStateTitle = state.getTitle();
                if (currentState == null) {
                    currentState = state;
                    return;
                }
            }
        }

        System.out.printf("(Debug): Unable to find state: '%s'%n", animationTitle);
    }
}
