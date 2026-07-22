package tabletop.ui.tabs;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import tabletop.main.ApplicationCore;
import tabletop.model.PinModel;
import tabletop.ui.theme.ColorPalette;
import tabletop.ui.core.ColorPicker;

/**
 * Represents the map pins and notes tab construction instance.
 *
 * @author Adi
 */
public class PinsTabBuilder {

    private final ApplicationCore applicationCore;
    private JPanel mainCardPanel;
    private CardLayout cardLayout;
    private JPanel listContainer;
    private JPanel detailsContainer;

    private boolean isUpdatingProgrammatically = false;

    public PinsTabBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    public JPanel buildTab() {
        JPanel tabContainer = new JPanel(new BorderLayout());
        tabContainer.setBackground(ColorPalette.BACKGROUND_DARK);

        this.cardLayout = new CardLayout();
        this.mainCardPanel = new JPanel(this.cardLayout);
        this.mainCardPanel.setBackground(ColorPalette.BACKGROUND_DARK);

        // --- View 1: The Pin List ---
        JPanel listWrapper = new JPanel(new BorderLayout());
        listWrapper.setBackground(ColorPalette.BACKGROUND_DARK);

        JLabel listHeader = new JLabel("  Select a Pin to Edit Notes:");
        listHeader.setForeground(ColorPalette.TEXT_LIGHT);
        listHeader.setFont(new Font("SansSerif", Font.BOLD, 14));
        listHeader.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        listWrapper.add(listHeader, BorderLayout.NORTH);

        this.listContainer = new JPanel();
        this.listContainer.setLayout(new BoxLayout(this.listContainer, BoxLayout.Y_AXIS));
        this.listContainer.setBackground(ColorPalette.BACKGROUND_DARK);

        JScrollPane listScroll = new JScrollPane(this.listContainer);
        listScroll.setBorder(null);
        listWrapper.add(listScroll, BorderLayout.CENTER);

        // --- View 2: The Pin Details/Notes Editor ---
        JPanel detailsWrapper = new JPanel(new BorderLayout());
        detailsWrapper.setBackground(ColorPalette.BACKGROUND_DARK);

        JButton backButton = new JButton("⬅ Back to Pins List");
        backButton.setBackground(ColorPalette.BUTTON_PRIMARY);
        backButton.setForeground(ColorPalette.TEXT_LIGHT);
        backButton.setFocusPainted(false);
        backButton.setOpaque(true);
        backButton.setBorderPainted(false);
        backButton.addActionListener(e -> {
            this.applicationCore.getToolState().setSelectedPinIndex(null);
            if(this.applicationCore.onPinSelectionChanged != null) this.applicationCore.onPinSelectionChanged.run();
            this.applicationCore.refreshDisplay();
        });
        detailsWrapper.add(backButton, BorderLayout.NORTH);

        this.detailsContainer = new JPanel();
        this.detailsContainer.setLayout(new BoxLayout(this.detailsContainer, BoxLayout.Y_AXIS));
        this.detailsContainer.setBackground(ColorPalette.BACKGROUND_DARK);

        JScrollPane detailsScroll = new JScrollPane(this.detailsContainer);
        detailsScroll.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        detailsWrapper.add(detailsScroll, BorderLayout.CENTER);

        this.mainCardPanel.add(listWrapper, "LIST");
        this.mainCardPanel.add(detailsWrapper, "DETAILS");
        tabContainer.add(this.mainCardPanel, BorderLayout.CENTER);

        this.applicationCore.onPinSelectionChanged = this::refreshViews;
        this.refreshViews();

        return tabContainer;
    }

    private void refreshViews() {
        this.isUpdatingProgrammatically = true;

        Integer selectedId = this.applicationCore.getToolState().getSelectedPinIndex();

        if(selectedId == null) {
            this.cardLayout.show(this.mainCardPanel, "LIST");
            this.buildListPanel();
        } else {
            this.cardLayout.show(this.mainCardPanel, "DETAILS");
            this.buildDetailsPanel(selectedId);
        }

        this.isUpdatingProgrammatically = false;
    }

    private void buildListPanel() {
        this.listContainer.removeAll();
        java.util.List<PinModel> pins = this.applicationCore.getDataState().getMapPins();

        for(int i = 0; i < pins.size(); i++) {
            PinModel pin = pins.get(i);
            final int index = i;

            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(ColorPalette.LIST_ROW_BACKGROUND);
            row.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            String shareIcon = pin.isShared() ? " (Shared)" : "";
            JLabel nameLabel = new JLabel(pin.getTitle() + shareIcon);
            nameLabel.setForeground(ColorPalette.TEXT_LIGHT);
            row.add(nameLabel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            buttonPanel.setBackground(ColorPalette.LIST_ROW_BACKGROUND);

            JButton jumpBtn = new JButton("Jump");
            jumpBtn.setBackground(ColorPalette.BUTTON_PRIMARY);
            jumpBtn.setForeground(ColorPalette.TEXT_LIGHT);
            jumpBtn.setFocusPainted(false);
            jumpBtn.setOpaque(true);
            jumpBtn.setBorderPainted(false);
            jumpBtn.setMargin(new Insets(2, 5, 2, 5));
            jumpBtn.addActionListener(e -> this.jumpToPin(pin, index));

            JButton editBtn = new JButton("Open Notes");
            editBtn.setBackground(ColorPalette.BUTTON_WARNING);
            editBtn.setForeground(ColorPalette.TEXT_LIGHT);
            editBtn.setFocusPainted(false);
            editBtn.setOpaque(true);
            editBtn.setBorderPainted(false);
            editBtn.setMargin(new Insets(2, 5, 2, 5));
            editBtn.addActionListener(e -> {
                this.applicationCore.getToolState().setSelectedPinIndex(index);
                this.refreshViews();
                this.applicationCore.refreshDisplay();
            });

            JButton deleteBtn = new JButton("X");
            deleteBtn.setBackground(ColorPalette.BUTTON_DANGER);
            deleteBtn.setForeground(ColorPalette.TEXT_LIGHT);
            deleteBtn.setFocusPainted(false);
            deleteBtn.setOpaque(true);
            deleteBtn.setBorderPainted(false);
            deleteBtn.setMargin(new Insets(2, 5, 2, 5));
            deleteBtn.addActionListener(e -> {
                this.applicationCore.getDataState().getMapPins().remove(pin);
                this.refreshViews();
                this.applicationCore.refreshDisplay();
            });

            buttonPanel.add(jumpBtn);
            buttonPanel.add(editBtn);
            buttonPanel.add(deleteBtn);
            row.add(buttonPanel, BorderLayout.EAST);

            this.listContainer.add(row);
            this.listContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        this.listContainer.revalidate();
        this.listContainer.repaint();
    }

    private void buildDetailsPanel(int index) {
        this.detailsContainer.removeAll();
        java.util.List<PinModel> pins = this.applicationCore.getDataState().getMapPins();

        if (index >= pins.size()) return;
        PinModel pin = pins.get(index);

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        headerPanel.setBackground(ColorPalette.BACKGROUND_DARK);

        JLabel titleLabel = new JLabel("Title:");
        titleLabel.setForeground(ColorPalette.TEXT_LIGHT);

        JTextField titleField = new JTextField(pin.getTitle(), 15);
        titleField.setBackground(ColorPalette.INPUT_BACKGROUND);
        titleField.setForeground(ColorPalette.TEXT_LIGHT);

        titleField.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                if(!isUpdatingProgrammatically) pin.setTitle(titleField.getText());
            }
        });

        headerPanel.add(titleLabel);
        headerPanel.add(titleField);
        this.detailsContainer.add(headerPanel);

        JPanel colorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        colorPanel.setBackground(ColorPalette.BACKGROUND_DARK);
        JLabel colorLabel = new JLabel("Marker Color:");
        colorLabel.setForeground(ColorPalette.TEXT_LIGHT);

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.onColorChanged = () -> {
            if(!this.isUpdatingProgrammatically) {
                pin.setPinColor(colorPicker.getSelectedColor());
                this.applicationCore.refreshDisplay();
            }
        };

        colorPanel.add(colorLabel);
        colorPanel.add(colorPicker);
        this.detailsContainer.add(colorPanel);

        JPanel sharePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        sharePanel.setBackground(ColorPalette.BACKGROUND_DARK);
        JLabel shareLabel = new JLabel("Visible to Players:");
        shareLabel.setForeground(ColorPalette.TEXT_LIGHT);

        JCheckBox shareBox = new JCheckBox();
        shareBox.setBackground(ColorPalette.BACKGROUND_DARK);
        shareBox.setFocusPainted(false);
        shareBox.setSelected(pin.isShared());
        shareBox.addActionListener(e -> {
            if(!this.isUpdatingProgrammatically) {
                pin.setShared(shareBox.isSelected());
                this.applicationCore.refreshDisplay();
            }
        });

        sharePanel.add(shareLabel);
        sharePanel.add(shareBox);
        this.detailsContainer.add(sharePanel);

        JLabel notesLabel = new JLabel("Secret DM Notes:");
        notesLabel.setForeground(ColorPalette.TEXT_LIGHT);
        this.detailsContainer.add(notesLabel);
        this.detailsContainer.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextArea notesArea = new JTextArea(pin.getDescription());
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBackground(ColorPalette.INPUT_BACKGROUND);
        notesArea.setForeground(ColorPalette.TEXT_LIGHT);
        notesArea.setFont(new Font("SansSerif", Font.PLAIN, 14));

        notesArea.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                if(!isUpdatingProgrammatically) pin.setDescription(notesArea.getText());
            }
        });

        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(0, 300));
        this.detailsContainer.add(notesScroll);

        this.detailsContainer.revalidate();
        this.detailsContainer.repaint();
    }

    private void jumpToPin(PinModel pin, int index) {
        int canvasWidth = this.applicationCore.getCanvasPanel().getWidth();
        int canvasHeight = this.applicationCore.getCanvasPanel().getHeight();
        double cellDim = this.applicationCore.getDataState().calculateCellDimension();

        double targetX = (canvasWidth / 2.0) - (pin.getPositionHorizontal() * cellDim);
        double targetY = (canvasHeight / 2.0) - (pin.getPositionVertical() * cellDim);

        this.applicationCore.getDataState().setPanHorizontal(targetX);
        this.applicationCore.getDataState().setPanVertical(targetY);
        this.applicationCore.getToolState().setSelectedPinIndex(index);
        this.refreshViews();
        this.applicationCore.refreshDisplay();
    }

    private abstract class SimpleDocumentListener implements DocumentListener {
        public abstract void update();
        @Override public void insertUpdate(DocumentEvent e) { update(); }
        @Override public void removeUpdate(DocumentEvent e) { update(); }
        @Override public void changedUpdate(DocumentEvent e) { update(); }
    }
}
