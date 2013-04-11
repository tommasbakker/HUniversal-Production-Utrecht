
/** 
* @author Arjen van Zanten
 * @author Ammar Abdulamir
 * @author Arjen van Zanten
 * @file KnowledgeDBClient.java
 * @brief A client to communicate with knowledge database.
 * @date Created: 2013-04-05
 * @section LICENSE
 * License: newBSD
 * Copyright © 2013, HU University of Applied Sciences Utrecht.
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of the HU University of Applied Sciences Utrecht nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE HU UNIVERSITY OF APPLIED SCIENCES UTRECHT
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
 * GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/
package rexos.libraries.knowledge;

package KnowledgeDBClient;

import com.mysql.jdbc.Connection;

import java.sql.*;
import java.util.ArrayList;

/**
 * A client to communicate with knowledge database.
 **/
public class KnowledgeDBClient {
    /**
     * @var String URL
     *
     * The odbc connection url.
     **/
    private static final String URL = "jdbc:mysql://145.89.191.131:3306/rexos_knowledge_base";

    /**
     * @var String USER
     *
     * The database username.
     **/
    private static final String USER = "rexos";

    /**
     * @var String PASS
     *
     * The database password.
     **/
    private static final String PASS = "soxer";

    /**
     * @var rexos.libraries.knowledge.KnowledgeDBClient client
     *
     * The only rexos.libraries.knowledge.KnowledgeDBClient instance.
     **/
    private static KnowledgeDBClient client;

    /**
     * @var Connection connection
     *
     * The mysql connection.
     **/
    private Connection connection;

    /**
     * Get current rexos.libraries.knowledge.KnowledgeDBClient instance.
     *
     * @return The current rexos.libraries.knowledge.KnowledgeDBClient.
     **/
    public static synchronized KnowledgeDBClient getClient() {
        if (client == null) {
            client = new KnowledgeDBClient();
        }

        return client;
    }

    /**
     * Private constructor to create a mysql conenction.
     **/
    private KnowledgeDBClient() {
        try {
            this.connection = (Connection) DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get column names in a string array.
     * @param resultSet The ResultSet from a statement execution.
     *
     * @return A string array of column names.
     **/
    public String[] getColumns(ResultSet resultSet) {
        ArrayList<String> columns = new ArrayList<String>();

        try {
            ResultSetMetaData metadata = resultSet.getMetaData();

            for (int i = 1; i <= metadata.getColumnCount(); i++) {
                columns.add(metadata.getColumnLabel(i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String[] ret = new String[columns.size()];
        columns.toArray(ret);

        return ret;
    }

    /**
     * Execute a single query statement.
     *
     * @param query The query to be executed.
     *
     * @return A ResultSet objected generated by the query.
     * @throws SQLException
     **/
    public ResultSet executeSelectQuery(String query) throws SQLException {
        Statement statement = connection.createStatement();

        return statement.executeQuery(query);
    }

    /**
     * Execute a single query statement with parameters.
     *
     * @param query The query to be executed.
     * @param parameters The parameters for the query in a consecutive order.
     *
     * @return A ResultSet objected generated by the query.
     * @throws SQLException
     **/
    public ResultSet executeSelectQuery(String query, Object[] parameters) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(query);

        for (int i = 0; i < parameters.length; i++) {
            statement.setString(i + 1, parameters[i].toString());
        }

        return statement.executeQuery();
    }
}