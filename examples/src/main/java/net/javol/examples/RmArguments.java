package net.javol.examples;

import net.javol.Command;
import net.javol.Option;
import net.javol.Param;

import java.util.List;

@Command
abstract class RmArguments {

  /**
   * ALLES TURISTEN UND NONTEKNISCHEN LOOKENSPEEPERS!
   * DAS KOMPUTERMASCHINE IST NICHT FUR DER GEFINGERPOKEN UND MITTENGRABEN!
   * ODERWISE IST EASY TO SCHNAPPEN DER SPRINGENWERK, BLOWENFUSEN UND POPPENCORKEN MIT SPITZENSPARKEN.
   * IST NICHT FUR GEWERKEN BEI DUMMKOPFEN.
   * DER RUBBERNECKEN SIGHTSEEREN KEEPEN DAS COTTONPICKEN HANDER IN DAS POCKETS MUSS.
   * ZO RELAXEN UND WATSCHEN DER BLINKENLICHTEN.
   *
   * @return a boolean
   */
  @Option(value = "recursive", mnemonic = 'r')
  abstract boolean recursive();

  /**
   * Use the force, Luke.
   */
  @Option(value = "force", mnemonic = 'f')
  abstract boolean force();

  /**
   * This is a list that may be empty.
   */
  @Param(1)
  abstract List<String> otherTokens();
}
