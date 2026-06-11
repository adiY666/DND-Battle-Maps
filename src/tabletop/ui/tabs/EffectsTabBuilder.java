package tabletop.ui.tabs;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import tabletop.main.ApplicationCore;
import tabletop.state.DataState;
import tabletop.model.TokenModel;
import tabletop.model.EffectModel;
import tabletop.ui.theme.ColorPalette;

/**
 * Represents the global effects tab construction instance.
 *
 * @author Adi
 */
public class EffectsTabBuilder {

    private final ApplicationCore applicationCore;
    private JPanel listContainer;

    public EffectsTabBuilder(ApplicationCore applicationCore) {
        super();
        this.applicationCore = applicationCore;
    }

    public JPanel buildTab() {
        JPanel tabContainer = new JPanel(new BorderLayout());
        tabContainer.setBackground(ColorPalette.BACKGROUND_DARK);

        this.listContainer = new JPanel();
        this.listContainer.setLayout(new BoxLayout(this.listContainer, BoxLayout.Y_AXIS));
        this.listContainer.setBackground(ColorPalette.BACKGROUND_DARK);

        JScrollPane scrollPane = new JScrollPane(this.listContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(ColorPalette.BACKGROUND_DARK);
        tabContainer.add(scrollPane, BorderLayout.CENTER);

        this.applicationCore.onEffectsChanged = this::refreshEffectsList;

        this.refreshEffectsList();
        return tabContainer;
    }

    public void refreshEffectsList() {
        this.listContainer.removeAll();
        DataState dataState = this.applicationCore.getDataState();

        // Temporary container to sort effects globally
        class EffectEntry {
            final EffectModel effect;
            final String tokenName;
            EffectEntry(EffectModel effect, String tokenName) {
                this.effect = effect;
                this.tokenName = tokenName;
            }
        }

        List<EffectEntry> allEffects = new ArrayList<>();
        for(TokenModel token : dataState.getActiveTokens().values()) {
            for(EffectModel effect : token.getActiveEffects()) {
                allEffects.add(new EffectEntry(effect, token.getDisplayName()));
            }
        }

        // Sort by least time to most time
        allEffects.sort(Comparator.comparingInt(e -> e.effect.getRemainingTurns()));

        for(EffectEntry entry : allEffects) {
            JPanel row = new JPanel(new BorderLayout());
            row.setBackground(ColorPalette.LIST_ROW_BACKGROUND);
            row.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            JLabel infoLabel = new JLabel(entry.effect.getEffectName() + " (" + entry.tokenName + ") - " + entry.effect.getRemainingTurns() + " turns");
            infoLabel.setForeground(ColorPalette.TEXT_LIGHT);
            row.add(infoLabel, BorderLayout.CENTER);

            this.listContainer.add(row);
            this.listContainer.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        this.listContainer.revalidate();
        this.listContainer.repaint();
    }
}

