package com.rm.darya.events;

/**
 * Created by alex
 */
public interface OnInteractionListener {
    void onFragmentEmptyAction(int fragmentId);
    <T> void onFragmentActionWithData(T data);
}
