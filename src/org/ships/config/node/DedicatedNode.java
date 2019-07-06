package org.ships.config.node;

import org.core.configuration.ConfigurationFile;
import org.core.configuration.ConfigurationNode;
import org.core.configuration.parser.StringParser;

import java.util.Optional;

public class DedicatedNode<V> {

    protected String[] node;
    protected StringParser<V> parser;
    protected String simpleName;

    public DedicatedNode(String simpleName, StringParser<V> parser, String... node){
        this.node = node;
        this.parser = parser;
        this.simpleName = simpleName;
    }

    public String getSimpleName(){
        return this.simpleName;
    }

    public StringParser<V> getParser(){
        return this.parser;
    }

    public String[] getNode(){
        return this.node;
    }

    public Optional<V> getValue(ConfigurationFile file){
        return file.parse(new ConfigurationNode(this.node), this.parser);
    }

    public void setValue(ConfigurationFile file, V value){
        file.set(new ConfigurationNode(this.node), this.parser, value);
    }
}
