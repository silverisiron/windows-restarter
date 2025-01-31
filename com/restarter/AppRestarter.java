package com.restarter;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AppRestarter {
    private static List<String> appsToRestart = new ArrayList<>();  // 앱 목록

    public static void main(String[] args) {

        appsToRestart.add("notepad.exe");
        appsToRestart.add("chrome.exe");
        appsToRestart.add("explorer.exe");

        // GUI
        JFrame frame = new JFrame("앱 재시작");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new FlowLayout());

        // GUI style
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 재시작 버튼
        JButton restartButton = new JButton("재시작");
        restartButton.addActionListener(e -> {
            try {
                List<String> runningProcesses = getRunningProcesses();

                for (String process : runningProcesses) {
                    killProcess(process);
                }

                restartApps();
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "오류가 발생했습니다: " + ex.getMessage());
            }
        });

        // 실행 앱 설정
        JButton settingsButton = new JButton("실행 앱 설정");
        settingsButton.addActionListener(e -> {
            openAppSettingsDialog(frame);
        });

        frame.add(restartButton);
        frame.add(settingsButton);
        frame.setVisible(true);
    }

    // tasklist
    private static List<String> getRunningProcesses() throws IOException {
        List<String> processes = new ArrayList<>();
        String line;
        Process process = Runtime.getRuntime().exec("tasklist");
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while ((line = reader.readLine()) != null) {
            if (line.contains(".exe")) { // 프로세스 이름에 .exe가 포함된 항목만 추출
                processes.add(line.split("\\s+")[0]);
            }
        }
        return processes;
    }

    // taskkill
    private static void killProcess(String processName) throws IOException {
        Runtime.getRuntime().exec("taskkill /F /IM " + processName);
    }

    // restart
    private static void restartApps() throws IOException {
        for (String app : appsToRestart) {
            Runtime.getRuntime().exec(app);
        }
    }

    // 실행 앱 설정 버튼
    private static void openAppSettingsDialog(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "실행 앱 설정", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());

        // 앱 목록
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String app : appsToRestart) {
            listModel.addElement(app);
        }
        JList<String> appList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(appList);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // 버튼
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("추가");
        JButton removeButton = new JButton("삭제");
        // 앱 추가
        addButton.addActionListener(e -> {
            String newApp = JOptionPane.showInputDialog(dialog, "추가할 앱 이름 (예: notepad.exe):");
            if (newApp != null && !newApp.isEmpty() && !appsToRestart.contains(newApp)) {
                appsToRestart.add(newApp);
                listModel.addElement(newApp);
            }
        });
        // 앱 삭제
        removeButton.addActionListener(e -> {
            String selectedApp = appList.getSelectedValue();
            if (selectedApp != null) {
                appsToRestart.remove(selectedApp);
                listModel.removeElement(selectedApp);
            } else {
                JOptionPane.showMessageDialog(dialog, "삭제할 앱을 선택하세요.");
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}
