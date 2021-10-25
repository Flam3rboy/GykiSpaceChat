package gykispace;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class GUI extends JFrame {
    private JButton send;
    private JTextField input;
    private JPanel mainPanel;
    private JTabbedPane tabbar;
    private Map<String, JScrollPane> scroller = new HashMap<>();
    public Map<String, JPanel> lists = new HashMap<>();
    private static GUI instance;
    private ImageIcon indicator = new ImageIcon(getClass().getResource("./indicator.png"));

    static public GUI getInstance() {
        if (instance == null) instance = new GUI();
        return instance;
    }

    private GUI() {
        setContentPane(mainPanel);
        setTitle("GykiSpace");
        setSize(800, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        input.grabFocus();

        input.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String room = tabbar.getTitleAt(tabbar.getSelectedIndex());
                    Client.getInstance().send(room, input.getText());
                    input.setText("");
                }
            }

        });

        tabbar.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                tabbar.setIconAt(tabbar.getSelectedIndex(), null);
            }
        });

        /*
        theme.setModel(new DefaultComboBoxModel<>(Arrays.stream(UIManager.getInstalledLookAndFeels()).map(UIManager.LookAndFeelInfo::getName).toArray()));

        GUI self = this;
        theme.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    for (var lnf : UIManager.getInstalledLookAndFeels()) {
                        if (lnf.getName().equals(theme.getSelectedItem())) {
                            System.out.println("set theme to: "+lnf.getClassName());
                            UIManager.setLookAndFeel(lnf.getClassName());
                            SwingUtilities.updateComponentTreeUI(self);
                            self.pack();
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println(theme.getSelectedItem());
            }
        });
         */
    }

    public void updateRooms() {
        Client.getInstance().rooms.forEach((name, address) -> {
            if (tabbar.indexOfTab(name) == -1) {
                var list = new JPanel();
                list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));

                var container = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                container.setLayout(new ScrollPaneLayout());
                scroller.put(name, container);

                lists.put(name, list);
                tabbar.addTab(name, null, container, address.getHostAddress());
            }
        });
    }

    public void log(String room, String value) {
        JLabel label = new JLabel(value);
        if (tabbar.indexOfTab(room) == -1) return;

        if (tabbar.getSelectedIndex() != tabbar.indexOfTab(room)) {
            tabbar.setIconAt(tabbar.getSelectedIndex(), indicator);
        }

        lists.get(room).add(label);
        scroller.get(room).validate();
        scroller.get(room).getVerticalScrollBar().setValue(scroller.get(room).getVerticalScrollBar().getMaximum());
    }

    public void message(String room, String username, String content) {
        String value;
        var messages = Client.getInstance().messages.get(room);
        if (messages.get(messages.size() - 1)[0].equals(username)) {
            // message is from the same user -> don't display username only content
            value = content;
        } else {
            value = String.format("<html><b>%s</b>\n%s</html>", username, content);
        }
        log(room, value);
    }

    public void log(String value) {
        if (tabbar.getSelectedIndex() == -1) return;
        this.log(tabbar.getTitleAt(tabbar.getSelectedIndex()), value);
    }

}
