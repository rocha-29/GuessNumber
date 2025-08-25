package com.guess.client;

import com.guess.common.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;

public class GameFrame extends JFrame {
    private final JTextField txtName = new JTextField("Daniel", 12);
    private final JTextField txtGuess = new JTextField(6);
    private final JLabel lblFeedback = new JLabel("Ingresa tu intento (1-100) y presiona Adivinar");
    private final JTextArea txtTop = new JTextArea(8, 28);
    private NetClient net;

    public GameFrame() {
        setTitle("Adivina el número – Multiusuario");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 360);
        setLocationRelativeTo(null);

        JButton btnGuess = new JButton("Adivinar");
        JButton btnTop = new JButton("Ver TOP");
        JButton btnCon = new JButton("Conectar");

        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT));
        north.add(new JLabel("Nombre:")); north.add(txtName);
        north.add(btnCon);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.LEFT));
        center.add(new JLabel("Tu número:"));
        center.add(txtGuess);
        center.add(btnGuess);
        center.add(btnTop);

        txtTop.setEditable(false);
        JScrollPane sp = new JScrollPane(txtTop);

        setLayout(new BorderLayout(8,8));
        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(lblFeedback, BorderLayout.SOUTH);
        add(sp, BorderLayout.EAST);

        btnCon.addActionListener(this::connect);
        btnGuess.addActionListener(this::guess);
        btnTop.addActionListener(this::loadTop);
    }

    private void connect(ActionEvent e) {
        try {
            if (net != null) net.close();
            net = new NetClient("localhost", 5000);
            lblFeedback.setText("Conectado. ¡Suerte!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo conectar: " + ex.getMessage());
        }
    }

    private void guess(ActionEvent e) {
        if (net == null) { JOptionPane.showMessageDialog(this, "Conéctate primero"); return; }
        try {
            int g = Integer.parseInt(txtGuess.getText().trim());
            Map<String,Object> payload = new HashMap<>();
            payload.put("name", txtName.getText().trim().isEmpty() ? "Jugador" : txtName.getText().trim());
            payload.put("guess", g);
            Response r = net.send(new Request(Command.GUESS, payload));
            if (!r.isOk()) { lblFeedback.setText(r.getMessage()); return; }
            @SuppressWarnings("unchecked")
            Map<String,Object> data = (Map<String,Object>) r.getData();
            String res = Objects.toString(data.get("result"), "?");
            if ("¡correcto!".equals(res)) {
                int attempts = (int) data.get("attempts");
                long ms = (long) data.get("millis");
                lblFeedback.setText("¡Correcto! Intentos: " + attempts + " — Tiempo: " + (ms/1000.0) + "s");
                loadTop(null);
            } else {
                lblFeedback.setText("El número es " + res);
            }
            txtGuess.setText("");
            txtGuess.requestFocus();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Número inválido");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void loadTop(ActionEvent e) {
        if (net == null) { JOptionPane.showMessageDialog(this, "Conéctate primero"); return; }
        try {
            Response r = net.send(new Request(Command.GET_TOP, null));
            if (!r.isOk()) { JOptionPane.showMessageDialog(this, r.getMessage()); return; }
            @SuppressWarnings("unchecked")
            java.util.List<Object> list = (java.util.List<Object>) r.getData();
            StringBuilder sb = new StringBuilder("🏆 TOP 5\n");
            int i = 1;
            for (Object o : list) {
                // ScoreEntry getters via reflexión para evitar compartir clase de modelo
                String name = (String) o.getClass().getMethod("getName").invoke(o);
                int attempts = (int) o.getClass().getMethod("getAttempts").invoke(o);
                long millis = (long) o.getClass().getMethod("getMillis").invoke(o);
                sb.append(i++).append(". ").append(name).append(" — ").append(attempts)
                  .append(" intentos — ").append(millis/1000.0).append("s\n");
            }
            txtTop.setText(sb.toString());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }
}
