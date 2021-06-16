package com.jacobcxdev.ev3mazeproject.hardware;

import lejos.hardware.port.Port;
import org.reflections8.Reflections;
import org.reflections8.scanners.SubTypesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An abstract class used to represent a part connected to one of the EV3's ports.
 */
public abstract class PartDefinition {
    // Public Enumerations

    /**
     * An enumeration to represent the different possible families of a part.
     */
    public enum Family {
        EV3,
        NXT
    }

    // Private Static Fields

    /**
     * The {@code Logger} for {@code PartDefinition} instances.
     */
    private static final Logger log = LoggerFactory.getLogger(PartDefinition.class);

    // Public Fields

    /**
     * The {@code Port} which the part is connected to.
     */
    public final Port port;

    /**
     * The {@code Family} of the part.
     */
    public final Family family;

    // Public Constructors

    /**
     * Creates a {@code PartDefinition} object.
     *
     * @param port The {@code Port} which the part is connected to.
     * @param family The {@code Family} of the part.
     */
    public PartDefinition(Port port, Family family) {
        this.port = port;
        this.family = family;
    }

    // Protected Methods

    /**
     * Gets a {@code Class} for a given unqualified name.
     *
     * @param name The unqualified name to get the {@code Class} for.
     * @param classStore A {@code Map} mapping the name of each {@code Class} used by the part to its respective {@code Class}.
     * @return A {@code Class} matching the given name.
     * @throws ClassNotFoundException If the given name does not match any {@code Class}.
     */
    protected Class<?> classForUnqualifiedName(String name, Map<String, Class<?>> classStore) throws ClassNotFoundException {
        if (classStore.isEmpty()) {
            var args = new ArrayList<Object>(Arrays.asList(getPartPackageNames()));
            args.add(new SubTypesScanner(false));
            classStore.putAll(new Reflections(args.toArray())
                .getSubTypesOf(Object.class).stream()
                .collect(Collectors.toMap(
                    (x) -> x.getName().substring(x.getName().lastIndexOf(".") + 1),
                    Function.identity(),
                    (x, y) -> {
                        log.error("Duplicate classname found @ {}", y.getName());
                        return x;
                    }
                ))
            );
        }
        return Optional.ofNullable(classStore.getOrDefault(name, null))
            .orElseThrow(() -> new ClassNotFoundException(name));
    }

    // Protected Abstract Methods

    /**
     * Gets the names of the packages housing classes used by the part.
     *
     * @return The names of the packages housing classes used by the part.
     */
    protected abstract String[] getPartPackageNames();

    // Public Abstract Methods

    /**
     * Creates a new instance of the part from the port and family.
     *
     * @return A new instance of the part as an {@code Object}.
     * @throws ClassNotFoundException If an error is encountered during hardware initialisation.
     * @throws NoSuchMethodException If an error is encountered during hardware initialisation.
     * @throws IllegalAccessException If an error is encountered during hardware initialisation.
     * @throws InvocationTargetException If an error is encountered during hardware initialisation.
     * @throws InstantiationException If an error is encountered during hardware initialisation.
     */
    public abstract Object newPartInstance() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException;
}
