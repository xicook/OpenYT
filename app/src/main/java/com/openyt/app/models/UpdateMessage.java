package com.openyt.app.models;

/**
 * Representa uma mensagem de update do servidor (lida de /opt/app_msg.txt).
 */
public class UpdateMessage {

    private int id;
    private String text;
    private String timestamp;

    public UpdateMessage() {}

    public UpdateMessage(int id, String text, String timestamp) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
    }

    public int getId() { return id; }
    public String getText() { return text; }
    public String getTimestamp() { return timestamp; }

    public void setId(int id) { this.id = id; }
    public void setText(String text) { this.text = text; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    /** Retorna a data formatada de forma simples */
    public String getFormattedDate() {
        if (timestamp == null || timestamp.isEmpty()) return "";
        // Pega só a parte da data do ISO 8601 (ex: "2024-01-15")
        if (timestamp.length() >= 10) {
            return timestamp.substring(0, 10);
        }
        return timestamp;
    }
}
