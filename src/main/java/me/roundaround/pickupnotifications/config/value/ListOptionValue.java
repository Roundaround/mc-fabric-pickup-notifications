package me.roundaround.pickupnotifications.config.value;

public interface ListOptionValue {
    String getId();
    String getDisplayString();
    ListOptionValue getFromId(String id);
    ListOptionValue getNext();
    ListOptionValue getPrev();
}
