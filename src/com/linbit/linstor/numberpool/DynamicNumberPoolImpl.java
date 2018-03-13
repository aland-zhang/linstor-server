package com.linbit.linstor.numberpool;

import com.linbit.Checks;
import com.linbit.ExhaustedPoolException;
import com.linbit.ImplementationError;
import com.linbit.ValueInUseException;
import com.linbit.ValueOutOfRangeException;
import com.linbit.linstor.core.Controller;
import com.linbit.linstor.logging.ErrorReporter;
import com.linbit.linstor.propscon.InvalidKeyException;
import com.linbit.linstor.propscon.Props;

import java.util.regex.Matcher;

public class DynamicNumberPoolImpl implements DynamicNumberPool
{
    private static final String OUT_OF_RANGE_EXC_FORMAT =
        " %d is out of range [%d - %d]";

    private static final String IN_USE_EXC_FORMAT =
        " %d is already in use";

    private final ErrorReporter errorReporter;
    private final Props ctrlConf;
    private final String ctrlConfKeyRange;
    private final String elementName;
    private final NumberRangeChecker rangeLimitChecker;
    private final int defaultMin;
    private final int defaultMax;

    private final NumberPool numberPool;

    private int rangeMin;
    private int rangeMax;

    public DynamicNumberPoolImpl(
        ErrorReporter errorReporterRef,
        Props ctrlConfRef,
        String ctrlConfKeyRangeRef,
        String elementNameRef,
        NumberRangeChecker rangeLimitCheckerRef,
        int hardMax,
        int defaultMinRef,
        int defaultMaxRef
    )
    {
        errorReporter = errorReporterRef;
        ctrlConf = ctrlConfRef;
        ctrlConfKeyRange = ctrlConfKeyRangeRef;
        elementName = elementNameRef;
        rangeLimitChecker = rangeLimitCheckerRef;
        defaultMin = defaultMinRef;
        defaultMax = defaultMaxRef;

        numberPool = new BitmapPool(hardMax + 1);
    }

    @Override
    public void reloadRange()
    {
        String strRange;
        try
        {
            strRange = ctrlConf.getProp(ctrlConfKeyRange);
            Matcher matcher;
            boolean useDefaults = true;

            if (strRange != null)
            {
                matcher = Controller.RANGE_PATTERN.matcher(strRange);
                if (matcher.find())
                {
                    try
                    {
                        rangeMin = Integer.parseInt(matcher.group("min"));
                        rangeMax = Integer.parseInt(matcher.group("max"));

                        rangeLimitChecker.check(rangeMin);
                        rangeLimitChecker.check(rangeMax);
                        useDefaults = false;
                    }
                    catch (ValueOutOfRangeException | NumberFormatException exc)
                    {
                        errorReporter.reportError(
                            exc,
                            null,
                            null,
                            "Unable to parse range from '" + strRange + "'"
                        );
                    }
                }
                else
                {
                    errorReporter.logError("Unable to extract range from '" + strRange + "'");
                }
            }
            if (useDefaults)
            {
                rangeMin = defaultMin;
                rangeMax = defaultMax;
            }
        }
        catch (InvalidKeyException invldKeyExc)
        {
            throw new ImplementationError(
                "Controller configuration key was invalid: " + invldKeyExc.invalidKey,
                invldKeyExc
            );
        }
    }

    @Override
    public int getRangeMin()
    {
        return rangeMin;
    }

    @Override
    public int getRangeMax()
    {
        return rangeMax;
    }

    @Override
    public void allocate(int nr)
        throws ValueOutOfRangeException, ValueInUseException
    {
        Checks.genericRangeCheck(nr, rangeMin, rangeMax, elementName + OUT_OF_RANGE_EXC_FORMAT);
        synchronized (numberPool)
        {
            if (numberPool.isAllocated(nr))
            {
                throw new ValueInUseException(String.format(elementName + IN_USE_EXC_FORMAT, nr));
            }
            numberPool.allocate(nr);
        }
    }

    @Override
    public int autoAllocate() throws ExhaustedPoolException
    {
        synchronized (numberPool)
        {
            return numberPool.autoAllocate(
                rangeMin,
                rangeMax
            );
        }
    }

    @Override
    public void deallocate(int nr)
    {
        synchronized (numberPool)
        {
            numberPool.deallocate(nr);
        }
    }

    public interface NumberRangeChecker
    {
        void check(Integer integer)
            throws ValueOutOfRangeException;
    }
}