package tabletop.ui.layout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;

import tabletop.main.ApplicationCore;
import tabletop.ui.tabs.TokensTabBuilder;
import tabletop.ui.tabs.NotesTabBuilder;
import tabletop.ui.tabs.EffectsTabBuilder;
import tabletop.ui.theme.ColorPalette;

/**
 * Represents the right panel construction instance.
 *
 * @author Adi
 */
public class RightPanelBuilder {

    private final ApplicationCore applicationCore;

    public RightPanelBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    public JPanel buildRightWrapper() {
        JPanel rightWrapper = new JPanel(new BorderLayout());
        rightWrapper.setBackground(ColorPalette.BACKGROUND_DARK);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(285, 0));
        rightPanel.setBackground(ColorPalette.BACKGROUND_DARK);

        JButton buttonToggle = new JButton("▶");
        buttonToggle.setMargin(new Insets(0, 0, 0, 0));
        buttonToggle.setBackground(ColorPalette.LIST_ROW_BACKGROUND);
        buttonToggle.setForeground(ColorPalette.TEXT_LIGHT);
        buttonToggle.setFocusPainted(false);

        buttonToggle.addActionListener(event -> {
            boolean isVisible = rightPanel.isVisible();
            rightPanel.setVisible(!isVisible);
            buttonToggle.setText(isVisible ? "◀" : "▶");
        });

        JTabbedPane rightNotebook = new JTabbedPane();

        TokensTabBuilder charactersTabBuilder = new TokensTabBuilder(this.applicationCore);
        EffectsTabBuilder effectsTabBuilder = new EffectsTabBuilder(this.applicationCore);
        NotesTabBuilder generalInfoTabBuilder = new NotesTabBuilder(this.applicationCore);

        rightNotebook.addTab("Characters", charactersTabBuilder.buildTab());
        rightNotebook.addTab("Effects", effectsTabBuilder.buildTab());
        rightNotebook.addTab("Notes", generalInfoTabBuilder.buildTab());

        rightPanel.add(rightNotebook, BorderLayout.CENTER);
        rightWrapper.add(rightPanel, BorderLayout.CENTER);
        rightWrapper.add(buttonToggle, BorderLayout.WEST);

        return rightWrapper;
    }
}
