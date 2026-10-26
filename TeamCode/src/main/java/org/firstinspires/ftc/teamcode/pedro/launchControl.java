package org.firstinspires.ftc.teamcode.pedro;

import androidx.annotation.Nullable;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.behaviors.BlockedBehavior;
import com.pedropathing.ivy.behaviors.ConflictBehavior;
import com.pedropathing.ivy.behaviors.EndCondition;
import com.pedropathing.ivy.behaviors.InterruptedBehavior;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class launchControl {


    /**
     * Bang-bang controller, tries to spin the provided motor to provided rpm.
     * If motor rpm under rpm target, full positive power
     * If over rpm target + overspeedTolerance, full negative power
     */

    public static Command spinTo(double rpm, DcMotorEx motor, double overspeedTolerance) {
        double motorRPM = motor.getVelocity()*(60.0/28.0);
        if (rpm > motorRPM) {
            return Command.build()
                    .setExecute(() -> motor.setPower(1));
        }else if (rpm+overspeedTolerance < motorRPM){
            return Command.build()
                    .setExecute(() -> {
                        motor.setPower(0);

                    });
        }else {
            return Command.build()
                    .setExecute(() -> {
                        motor.setPower(rpm/4500);

                    });
        }

    }
}
