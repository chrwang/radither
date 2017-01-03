//Imports

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A model of using radiation to treat a solid cancerous mass of hypoxic and oxygenated cells.
 * <p>
 * This model models a system of cancerous cells, treated with LET-ionising radiation. The model makes certain
 * assumptions, which are enumerated in the paper. Oxygenated and hypoxic cells are differentiated between; they die at
 * different rates, only oxygenated cells are able to divide, and the ratios must stay the same.
 * <p>
 * This model is based on earlier computational biology works by Fischer at Yale University, various biological
 * research done by Doida et. al, Hewitt et. al, material from professors at MIT OPENCOURSEWARE, and others. We thank
 * them for their work.
 *
 * @author A. Senapati
 * @author C. Wang
 *         <p>
 *         This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0. If a copy of the MPL was
 *         not distributed with this file, You can obtain one at https://mozilla.org/MPL/2.0/.
 * @version 0.1.0-alpha1
 *          This project follows semantic versioning guidelines, outlines at https://semver.org.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class RadiationTreatment implements Serializable {
    //Constants

    //Serial Version UID. Used during serialisation of objects to keep track of distinct versions. Remains the same
    //unless major data serialisation changes are made in this source file.
    private static final long serialVersionUID = -6108015401854170786L;

    //The tumor is made up of two types of cells, oxygenated cells and hypoxic cells. Oxygenated cells multiply at a
    //rate of alpha, while hypoxic cells do not multiply. When dead oxygenated cells attempt to reproduce, they
    //disintegrate and are removed from the count.

    //Variables used throughout the model

    static final int numTreat = 50;

    //hit or extrapolation number for oxygenated cells
    static Double no = 4.0;

    //characteristic dose for oxygenated cells
    static Double Do = 100.0;

    //hit or extrapolation for hypoxic cells
    static Double na = 1.0;

    //characteristic dose for hypoxic cells;
    static Double Da = 250.0;

    //The probability of a cell surviving given a dose D is S = 1-(1-e^(D/Do))^n
    //This is derived from the multi-target single-hit model, as explained in the paper

    //The initial cell count of the tumor
    static Double initCellCount = Math.pow(10, 10);

    //The rate at which oxygenated cells divide
    static Double alpha = .004;
    static Double beta = Math.pow(10, -10);
    static Double gamma = .005;

    //A list of living, oxygenated cell counts
    static ArrayList<Double> liveox;

    //A list of dead, oxygenated cell counts
    static ArrayList<Double> deadox;

    //A list of living, hypoxic cell counts
    static ArrayList<Double> liveanox;

    //A list of dead, hypoxic cell counts.
    static ArrayList<Double> deadanox;

    //A counter for recording data points
    static int number = 0;

    //The radiation dose, in rads, for this treatment
    static int D = 200;

    //Ratio of oxygenated to anoxic after reproduction.
    static double R_II;

    //Ratio of oxygenated to anoxic after balancing
    static double R_III;

    //Total number of cells
    static double N_III;

    /**
     * The main method of the programme. Runs the simulation and outputs the data.
     * <p>
     * This method initialises all of the variables not initialised before and begins the treatment regiment. It outputs
     * the data in nicely formatted text files.
     *
     * @param args Command line arguments, currently unused
     */
    public static void main(String[] args) {

        //Initialise ArrayLists for keeping track of data
        liveox = new ArrayList<>();
        deadox = new ArrayList<>();
        liveanox = new ArrayList<>();
        deadanox = new ArrayList<>();

        //Add initial values for dead cells; there are none at the beginning so the value is zero.
        deadox.add(0.0);
        deadanox.add(0.0);

        //Add initial values for living cells. The proportions of oxygenated and hypoxic are taken from Fischer's 1969
        //paper on radiation therapy.
        liveox.add(initCellCount * .36875);
        liveanox.add(initCellCount * .63125);

        //The treatment loop. Every increment of i represents one treatment. Performs numTreat number of treatments.
        for (int i = 0; i < numTreat; i++) {
            //Conduct the radiation treatment, calculates cell deaths
            afterRadiation();
            //increment the array index for data recording
            number++;
            //The cells attempt to divide. Dead cells removed, hypoxic don't divide, updates counts.
            afterReproduction();
            //increment the array index for data recording
            number++;
            //rebalance oxygenation ratios in the tumour.
            afterOxygenation();
        }

    }

    /**
     * Calculates the cell numbers after the radiation treatment is performed.
     * No params, automatically puts values into the ArrayLists.
     */
    static void afterRadiation() {
        //performs radiation calculations for oxygenated cell deaths
        liveox.add(liveox.get(number) * (1 - Math.pow(1 - Math.pow(Math.E, D / Do), no)));
        deadox.add(liveox.get(number) + deadox.get(number) - liveox.get(number + 1));

        //performs radiation calculations for hypoxic cell deaths
        liveanox.add(liveanox.get(number) * (1 - Math.pow(1 - Math.pow(Math.E, D / Da), na)));
        deadanox.add(liveanox.get(number) + deadanox.get(number) - liveanox.get(number + 1));

    }

    /**
     * Calculates the cell numbers after the cells attempt to reproduce.
     * No params, automatically puts values into the ArrayLists.
     */
    static void afterReproduction() {

        //This accounts for a mitotic delay in cell division. Cells do not immediately divide.
        double time = 1;
        //oxygenated cells reproduce with rate alpha and then die after reproducing making those cells
        double deltaT = gamma * D;
        double truetime = time - deltaT;

        //Living oxygenated cells divide by a certain exponential factor
        liveox.add(liveox.get(number) * Math.pow(Math.E, alpha * truetime));
        //The dead cells which attempt to divide are counted.
        deadox.add(deadox.get(number) * Math.pow(Math.E, -alpha * truetime));

        //hypoxic cells do not reproduce, so the numbers stay the same
        liveanox.add(liveanox.get(number));
        //Ibid.
        deadanox.add(deadanox.get(number));

    }

    /**
     * Calculates the cell numbers after the cells rebalance the oxygenation-anoxic ratio.
     * No params, automatically puts values into the ArrayLists.
     */
    static void afterOxygenation() {
        //Total number of cells at this point
        N_III = liveanox.get(number) + liveox.get(number) + deadanox.get(number) + deadox.get(number);

        //Calculates expected ratio
        R_III = Math.pow(Math.E, -beta * N_III);

        //Calculates the current ratio
        R_II = (liveox.get(number) + deadox.get(number)) / (liveanox.get(number) + deadanox.get(number));

        //Constant for ratio adjustment
        double L = N_III / ((1 + R_III) * (1 + liveanox.get(number) / deadanox.get(number)));

        //Another constant for ratio adjustment
        double M = R_III * N_III / ((1 + R_III) * (1 + liveanox.get(number) / deadanox.get(number)));

        //Adjustment control sequence
        if (R_III > R_II) { //If imbalance, RIII too large

            //Reoxygenation of some anoxic live cells
            liveox.add(liveox.get(number) + liveanox.get(number) - liveanox.get(number) / deadanox.get(number) * L);

            //Reoxygenation of some anoxic dead cells
            deadox.add(deadox.get(number) + deadanox.get(number) - L);

            //Decrease in anoxic cells
            liveanox.add(liveanox.get(number) / deadanox.get(number) * L);
            deadanox.add(L);

        } else if (R_III < R_II) { //If imbalance, RIII too small

            //Decrease in oxygenated cells
            liveox.add(liveox.get(number) / liveox.get(number) * M);
            deadox.add(M);

            //Deoxygenation of some previously oxygenated living cells
            liveanox.add(liveox.get(number) + liveanox.get(number) - liveox.get(number) / liveox.get(number) * M);

            //Deoxygenation of some previously oxygenated dead cells
            deadanox.add(deadox.get(number) + deadanox.get(number) - M);
        }
        else{//If ratios already balanced
            //Add current values again
            liveox.add(liveox.get(number));
            liveanox.add(liveanox.get(number));
            deadox.add(deadox.get(number));
            deadanox.add(deadanox.get(number));

        }
    }

}
