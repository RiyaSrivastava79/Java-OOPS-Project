import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                BusBookingFrame f = new BusBookingFrame();
                f.setVisible(true);
            }
        });
    }
}

class BusBookingFrame extends JFrame {
    private final java.util.List<Bus> buses = new ArrayList<Bus>();
    private JComboBox<String> busCombo;
    private JComboBox<String> timeCombo;
    private JLabel routeLabel;
    private JPanel seatPanel;
    private JTextField nameField;
    private JTextArea bookingsArea;
    private Map<JToggleButton, Seat> buttonSeatMap = new LinkedHashMap<JToggleButton, Seat>();

    BusBookingFrame() {
        setTitle("Simple Bus Booking");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(860, 540);
        setLocationRelativeTo(null);
        initData();
        initUI();
        if (!buses.isEmpty()) loadBus(0);
    }

    private void initData() {
        buses.add(new Bus("Delhi Bus 1", "Delhi", "Jaipur",
                Arrays.asList(new String[]{"08:30 AM", "11:00 AM", "05:45 PM"}), 4, 5));
        buses.add(new Bus("Delhi Bus 2", "Delhi", "Varanasi",
                Arrays.asList(new String[]{"07:00 AM", "02:15 PM", "09:00 PM"}), 4, 5));

        buses.get(0).bookSeat("08:30 AM", 0, 0, "Samanyu");
    buses.get(0).bookSeat("08:30 AM", 0, 1, "Aarav");
    buses.get(0).bookSeat("08:30 AM", 1, 3, "Ishita");
    buses.get(0).bookSeat("08:30 AM", 2, 4, "Riya");
    buses.get(0).bookSeat("08:30 AM", 3, 0, "Devansh");

    buses.get(0).bookSeat("11:00 AM", 0, 2, "Kavya");
    buses.get(0).bookSeat("11:00 AM", 1, 1, "Pranav");
    buses.get(0).bookSeat("11:00 AM", 2, 0, "Diya");
    buses.get(0).bookSeat("11:00 AM", 3, 3, "Aditya");
    buses.get(0).bookSeat("11:00 AM", 3, 4, "Saanvi");

    buses.get(0).bookSeat("05:45 PM", 0, 3, "Ananya");
    buses.get(0).bookSeat("05:45 PM", 1, 2, "Riya");
    buses.get(0).bookSeat("05:45 PM", 2, 1, "Aryan");
    buses.get(0).bookSeat("05:45 PM", 3, 2, "Meera");
    buses.get(0).bookSeat("05:45 PM", 3, 4, "Ishaan");

    // --- Pre-book seats for Bus B ---
    buses.get(1).bookSeat("07:00 AM", 0, 0, "Tanvi");
    buses.get(1).bookSeat("07:00 AM", 0, 1, "Raghav");
    buses.get(1).bookSeat("07:00 AM", 1, 3, "Aisha");
    buses.get(1).bookSeat("07:00 AM", 2, 2, "Karan");
    buses.get(1).bookSeat("07:00 AM", 3, 4, "Niharika");

    buses.get(1).bookSeat("02:15 PM", 0, 2, "Hriday");
    buses.get(1).bookSeat("02:15 PM", 1, 1, "Sneha");
    buses.get(1).bookSeat("02:15 PM", 2, 0, "Yash");
    buses.get(1).bookSeat("02:15 PM", 2, 3, "Manvi");
    buses.get(1).bookSeat("02:15 PM", 3, 2, "Ira");

    buses.get(1).bookSeat("09:00 PM", 0, 0, "Aarohi");
    buses.get(1).bookSeat("09:00 PM", 0, 4, "Ritvik");
    buses.get(1).bookSeat("09:00 PM", 1, 2, "Tanishq");
    buses.get(1).bookSeat("09:00 PM", 2, 1, "Pooja");
    buses.get(1).bookSeat("09:00 PM", 3, 3, "Vivek");
    }

    private void initUI() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));

        busCombo = new JComboBox<String>();
        for (Bus b : buses) busCombo.addItem(b.name);
        busCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int idx = busCombo.getSelectedIndex();
                if (idx >= 0) loadBus(idx);
            }
        });
        top.add(new JLabel("Select bus:"));
        top.add(busCombo);

        routeLabel = new JLabel();
        routeLabel.setFont(routeLabel.getFont().deriveFont(Font.BOLD));
        top.add(new JLabel("Route:"));
        top.add(routeLabel);

        timeCombo = new JComboBox<String>();
        timeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int bi = busCombo.getSelectedIndex();
                Object sel = timeCombo.getSelectedItem();
                String time = sel == null ? null : sel.toString();
                if (bi >= 0 && time != null) loadSeatsFor(buses.get(bi), time);
            }
        });
        top.add(new JLabel("Departure time:"));
        top.add(timeCombo);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int bi = busCombo.getSelectedIndex();
                if (bi >= 0) loadBus(bi);
            }
        });
        top.add(refresh);

        seatPanel = new JPanel();
        seatPanel.setBorder(BorderFactory.createTitledBorder("Seats"));

        JPanel right = new JPanel(new BorderLayout(6,6));
        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        nameField = new JTextField(12);
        form.add(new JLabel("Passenger:"));
        form.add(nameField);
        JButton bookBtn = new JButton("Book Selected");
        bookBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { bookSelected(); }
        });
        JButton cancelBtn = new JButton("Cancel Selected");
        cancelBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { cancelSelected(); }
        });
        form.add(bookBtn);
        form.add(cancelBtn);

        bookingsArea = new JTextArea(16, 34);
        bookingsArea.setEditable(false);
        right.add(form, BorderLayout.NORTH);
        right.add(new JScrollPane(bookingsArea), BorderLayout.CENTER);

        getContentPane().setLayout(new BorderLayout(8,8));
        getContentPane().add(top, BorderLayout.NORTH);
        getContentPane().add(seatPanel, BorderLayout.CENTER);
        getContentPane().add(right, BorderLayout.EAST);
    }

    private void loadBus(int index) {
        if (index < 0 || index >= buses.size()) return;
        Bus bus = buses.get(index);
        routeLabel.setText(bus.origin + " \u2192 " + bus.destination);

        timeCombo.removeAllItems();
        for (String t : bus.times) timeCombo.addItem(t);
        if (!bus.times.isEmpty()) timeCombo.setSelectedIndex(0);
    }

    private void loadSeatsFor(Bus bus, String time) {
        seatPanel.removeAll();
        buttonSeatMap.clear();
        seatPanel.setLayout(new GridLayout(bus.rows, bus.cols, 6, 6));

        for (int r = 0; r < bus.rows; r++) {
            for (int c = 0; c < bus.cols; c++) {
                Seat s = bus.getSeat(time, r, c);
                final JToggleButton btn = new JToggleButton(s.label());
                btn.setMargin(new Insets(2,2,2,2));
                btn.setSelected(false);
                btn.setFocusPainted(false);
                btn.setOpaque(true); // ensure background color is visible
                if (s.booked) {
                    btn.setBackground(Color.PINK);
                    btn.setToolTipText("Booked by: " + s.passenger);
                } else {
                    btn.setBackground(UIManager.getColor("Button.background"));
                    btn.setToolTipText("Free");
                }
                buttonSeatMap.put(btn, s);
                seatPanel.add(btn);
            }
        }
        updateBookingsArea(bus, time);
        seatPanel.revalidate();
        seatPanel.repaint();
    }

    private void bookSelected() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (name.length() == 0) {
            JOptionPane.showMessageDialog(this, "Enter passenger name.", "Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int bi = busCombo.getSelectedIndex();
        Object sel = timeCombo.getSelectedItem();
        if (bi < 0 || sel == null) return;
        String time = sel.toString();

        int booked = 0;
        for (Map.Entry<JToggleButton, Seat> entry : buttonSeatMap.entrySet()) {
            JToggleButton btn = entry.getKey();
            Seat s = entry.getValue();
            if (btn.isSelected() && !s.booked) {
                s.booked = true;
                s.passenger = name;
                btn.setBackground(Color.PINK);
                btn.setToolTipText("Booked by: " + name);
                btn.setSelected(false);
                booked++;
            }
        }
        if (booked == 0) {
            JOptionPane.showMessageDialog(this, "No free seats selected.", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        updateBookingsArea(buses.get(bi), time);
    }

    private void cancelSelected() {
        int bi = busCombo.getSelectedIndex();
        Object sel = timeCombo.getSelectedItem();
        if (bi < 0 || sel == null) return;
        String time = sel.toString();

        int cancelled = 0;
        for (Map.Entry<JToggleButton, Seat> entry : buttonSeatMap.entrySet()) {
            JToggleButton btn = entry.getKey();
            Seat s = entry.getValue();
            if (btn.isSelected() && s.booked) {
                s.booked = false;
                s.passenger = null;
                btn.setBackground(UIManager.getColor("Button.background"));
                btn.setToolTipText("Free");
                btn.setSelected(false);
                cancelled++;
            }
        }
        if (cancelled == 0) {
            JOptionPane.showMessageDialog(this, "No booked seats selected for cancellation.\n(Select booked seat buttons to cancel)", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        updateBookingsArea(buses.get(bi), time);
    }

    private void updateBookingsArea(Bus bus, String time) {
        StringBuilder sb = new StringBuilder();
        sb.append("Bookings for ").append(bus.name)
          .append(" (").append(bus.origin).append(" \u2192 ").append(bus.destination).append(", ")
          .append(time).append("):\n");
        for (Seat s : bus.allSeats(time)) {
            if (s.booked) {
                sb.append(s.label()).append(" - ").append(s.passenger == null ? "" : s.passenger).append("\n");
            }
        }
        bookingsArea.setText(sb.toString());
    }
}

class Bus {
    final String name;
    final String origin;
    final String destination;
    final java.util.List<String> times;
    final int rows, cols;
    private final Map<String, Seat[][]> seatsByTime = new LinkedHashMap<String, Seat[][]>();

    Bus(String name, String origin, String destination, java.util.List<String> times, int rows, int cols) {
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.times = new ArrayList<String>(times);
        this.rows = rows;
        this.cols = cols;
        for (String t : this.times) {
            Seat[][] grid = new Seat[rows][cols];
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    grid[r][c] = new Seat(r, c);
            seatsByTime.put(t, grid);
        }
    }

    Seat getSeat(String time, int r, int c) {
        Seat[][] grid = seatsByTime.get(time);
        if (grid == null) throw new IllegalArgumentException("Unknown time: " + time);
        return grid[r][c];
    }

    java.util.List<Seat> allSeats(String time) {
        Seat[][] grid = seatsByTime.get(time);
        java.util.List<Seat> list = new ArrayList<Seat>();
        if (grid != null) {
            for (int r = 0; r < rows; r++)
                for (int c = 0; c < cols; c++)
                    list.add(grid[r][c]);
        }
        return list;
    }

    void bookSeat(String time, int r, int c, String passenger) {
        Seat s = getSeat(time, r, c);
        s.booked = true;
        s.passenger = passenger;
    }
}

class Seat {
    final int row, col;
    boolean booked = false;
    String passenger = null;

    Seat(int r,int c){ row=r; col=c; }

    String label(){
        char letter = (char)('A' + row);
        return String.valueOf(letter) + String.valueOf(col + 1);
    }
}
