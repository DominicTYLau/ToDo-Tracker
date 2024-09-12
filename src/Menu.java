
import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import javax.swing.*;

public class Menu extends JFrame {

    private final JFrame frame;
    private JPanel panel = new JPanel();
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private JPanel notesPanel = new JPanel();
    private JPanel leftPanel = new JPanel();
    private JPanel rightPanel;
    private final JLabel todoLabel = new JLabel("ToDo");
    private final JLabel doneLabel = new JLabel("Done");
    private LocalDate today = LocalDate.now();
    private String[][] prioritize = {};

    private final JPanel doneNotesPanel = new JPanel();

    public Menu() {
        this.rightPanel = new JPanel();
        // Initialize frame and layout

        frame = new JFrame();
        frame.setLayout(null);
        frame.setSize(screenSize);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the left and right layout
        panel = new JPanel();
        panel.setLayout(new GridLayout(1, 2));
        panel.setSize(screenSize);

        // Contains to do notes
        leftPanel = new JPanel();
        leftPanel.setSize(screenSize.width / 2, screenSize.height);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));

        // Contains done notes
        rightPanel = new JPanel();
        rightPanel.setSize(screenSize.width / 2, screenSize.height);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));

        this.notesPanel = new JPanel();
        this.notesPanel.setLayout(new WrapLayout());
        this.notesPanel.setSize(screenSize.width / 2, screenSize.height);

        // Add Scroll to notesPanel
        JScrollPane notesScrollPane = new JScrollPane(notesPanel);
        notesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        notesScrollPane.setPreferredSize(new Dimension(screenSize.width / 2, screenSize.height - 200));

        doneNotesPanel.setLayout(new WrapLayout());
        doneNotesPanel.setSize(screenSize.width / 2, screenSize.height);

        // Add Scroll to doneNotesPanel
        JScrollPane doneNotesScrollPane = new JScrollPane(doneNotesPanel);
        doneNotesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        doneNotesScrollPane.setPreferredSize(new Dimension(screenSize.width / 2, screenSize.height - 200));

        start(); // Add the notes to the notesPanel from the file

        // Add buttons to the bottom
        JButton addNote = new JButton("Add Note");
        addNote.setSize(screenSize.width / 3, 200);
        JPanel buttonsPanel = new JPanel();

        addNote.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Note note = new Note();

                addNoteButtonsPanel(note);

                // Add the Note JPanel to its parent
                notesPanel.add(note);
                notesPanel.revalidate();
                notesPanel.repaint();
            }
        });

        JButton prioritizeButton = new JButton("Prioritize");
        prioritizeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int numNotes = 0;
                // Count the number of notes
                for (Component component : notesPanel.getComponents()) {
                    if (component instanceof JPanel) {
                        numNotes++;
                    }
                    prioritize = new String[numNotes][5];
                }
                numNotes = 0;
                // Add the notes to the prioritize array
                for (Component component : notesPanel.getComponents()) {
                    if (component instanceof JPanel) {
                        Note note = (Note) component;
                        String dueDateMonth = note.getDueDateMonth();
                        String dueDateDay = note.getDueDateDay();
                        String priority = note.getPriority();
                        double totalScore;
                        int priorityScore;
                        long urgencyScore;

                        // Determine priority score based off of due date and importance
                        priorityScore = switch (priority) {
                            case "Critical" ->
                                5;
                            case "High" ->
                                4;
                            case "Normal" ->
                                3;
                            case "Low" ->
                                2;
                            default ->
                                0;
                        };

                        if (dueDateDay == null || dueDateMonth == null) {
                            urgencyScore = 0;
                        } else {
                            // Calculate urgency score
                            try {
                                long daysTillDue = ChronoUnit.DAYS.between(today, LocalDate.of(2024, stringMonthToInt(dueDateMonth), Integer.parseInt(dueDateDay)));

                                if (daysTillDue < 0) {
                                    urgencyScore = 7;
                                } else if (daysTillDue == 0) {
                                    urgencyScore = 5;
                                } else if (daysTillDue < 7) {
                                    urgencyScore = 3;
                                } else {
                                    urgencyScore = 1;
                                }
                            } catch (Exception ex) {
                                urgencyScore = 0;
                            }
                        }

                        totalScore = priorityScore * 1.1 + urgencyScore * 1.5;

                        prioritize[numNotes][0] = totalScore + "";
                        prioritize[numNotes][1] = note.getDescription();
                        prioritize[numNotes][3] = note.getDueDateDay();
                        prioritize[numNotes][2] = note.getDueDateMonth();
                        prioritize[numNotes][4] = note.getPriority();
                        numNotes++;
                    }
                }

                // Sort to do notes from least to most important
                for (numNotes = 0; numNotes < prioritize.length; numNotes++) {
                    for (int j = 0; j < prioritize.length - numNotes - 1; j++) {
                        if (Double.parseDouble(prioritize[j][0]) > Double.parseDouble(prioritize[j + 1][0])) {
                            String[] holder = prioritize[j];
                            prioritize[j] = prioritize[j + 1];
                            prioritize[j + 1] = holder;
                        }
                    }
                }
                // Remove all notes from notesPanel
                notesPanel.removeAll();

                // Add back the sorted notes from array
                for (numNotes = prioritize.length - 1; numNotes >= 0; numNotes--) {
                    Note note = new Note(prioritize[numNotes][1], prioritize[numNotes][2], prioritize[numNotes][3], prioritize[numNotes][4]);

                    addNoteButtonsPanel(note);

                    notesPanel.add(note);
                    notesPanel.revalidate();
                    notesPanel.repaint();
                }

            }
        });

        buttonsPanel.add(addNote);
        buttonsPanel.add(prioritizeButton);

        // Format heading labels
        todoLabel.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 50));
        todoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        doneLabel.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 50));
        doneLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Add panels to the frame
        leftPanel.add(todoLabel);
        leftPanel.add(notesScrollPane);
        leftPanel.add(buttonsPanel);

        rightPanel.add(doneLabel);
        rightPanel.add(doneNotesScrollPane);

        panel.add(leftPanel);
        panel.add(rightPanel);
        frame.add(panel);

        // When application is closed, call onQuit
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                onQuit();
            }
        });

        frame.setVisible(true);
    }

    private int stringMonthToInt(String month) {
        return switch (month) {
            case "January" ->
                1;
            case "February" ->
                2;
            case "March" ->
                3;
            case "April" ->
                4;
            case "May" ->
                5;
            case "June" ->
                6;
            case "July" ->
                7;
            case "August" ->
                8;
            case "September" ->
                9;
            case "October" ->
                10;
            case "November" ->
                11;
            case "December" ->
                12;
            default ->
                0;
        };
    }

    private void onQuit() {
        try {
            FileWriter fw = new FileWriter("notes.txt", false); //make file
            PrintWriter pw = new PrintWriter(fw);

            for (Component component : notesPanel.getComponents()) {

                if (component instanceof JPanel) {
                    Note note = (Note) component;

                    pw.println(note.getDescription()); // Add name of the note
                    pw.println(note.getDueDateMonth()); // Add due month
                    pw.println(note.getDueDateDay()); // Add due day
                    pw.println(note.getPriority()); // Add priority to the file

                }
            }
            pw.close();
            fw.close();
        } catch (IOException e) {
            System.err.println("Error in File writing");
        }
    }

    private void start() {
        try {
            FileReader fr = new FileReader("notes.txt");
            BufferedReader br = new BufferedReader(fr);
            String data = br.readLine();
            // Loop through all scores
            while (data != null) {
                // Get all the data to make new note
                String description = data;
                String dueDateMonth = br.readLine();
                String dueDateDay = br.readLine();
                String priority = br.readLine();

                Note note = new Note(description, dueDateMonth, dueDateDay, priority);

                addNoteButtonsPanel(note);

                notesPanel.add(note);
                notesPanel.revalidate();
                notesPanel.repaint();

                data = br.readLine(); // Gets next line in document

            }
            br.close();
        } catch (IOException e) {
            System.out.println("Error in File reading");
        }
    }

    private void addNoteButtonsPanel(Note note) {
        // Adds delete and done buttons to note panel which is for all todo notes
        JButton deleteButton = new JButton("Delete");
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Remove the Note JPanel from its parent
                notesPanel.remove(note);
                notesPanel.revalidate();
                notesPanel.repaint();
            }
        });

        JButton doneButton = new JButton("Done");

        doneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Get the note's information
                String description = note.getDescription();
                String dueDateMonth = note.getDueDateMonth();
                String dueDateDay = note.getDueDateDay();
                String priority = note.getPriority();

                // Add the done note panel to the done notes panel
                doneNotesPanel.add(new Note(description, dueDateMonth, dueDateDay, priority));
                doneNotesPanel.revalidate();
                doneNotesPanel.repaint();

                // Remove the Note JPanel from its parent
                notesPanel.remove(note);
                notesPanel.revalidate();
                notesPanel.repaint();
            }
        });

        // Add delete and done buttons to note panel
        JPanel noteButtonsPanel = new JPanel();
        noteButtonsPanel.add(deleteButton);
        noteButtonsPanel.add(doneButton);

        note.add(noteButtonsPanel);
    }

    // public static void main(String[] args) {
    //     new Menu();
    // }
}
