package i5.las2peer.services.faq;

import java.net.HttpURLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.fasterxml.jackson.core.JsonProcessingException;

import i5.las2peer.api.Context;
import i5.las2peer.restMapper.RESTService;
import i5.las2peer.restMapper.annotations.ServicePath;
import i5.las2peer.services.faq.database.DatabaseManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Contact;
import io.swagger.annotations.Info;
import io.swagger.annotations.License;
import io.swagger.annotations.SwaggerDefinition;
import io.swagger.jaxrs.Reader;
import io.swagger.models.Swagger;
import io.swagger.util.Json;

/**
 * 
 * FAQService
 * 
 * This microservice was generated by the CAE (Community Application Editor). If you edit it, please make sure to keep
 * the general structure of the file and only add the body of the methods provided in this main file. Private methods
 * are also allowed, but any "deeper" functionality should be outsourced to (imported) classes.
 * 
 */
@ServicePath("faq")
@Api
@SwaggerDefinition(
		info = @Info(
				title = "FAQService",
				version = "1.0",
				description = "A LAS2peer microservice generated by the CAE.",
				termsOfService = "none",
				contact = @Contact(
						name = "Jonas K",
						email = "CAEAddress@gmail.com"),
				license = @License(
						name = "BSD",
						url = "https://github.com/CAE-Community-Application-Editor/microservice-FAQService/blob/master/LICENSE.txt")))
public class FAQ extends RESTService {

	public final static String QUESTION_KEY = "question";
	public final static String ANSWER_KEY = "answer";

	/*
	 * Database configuration
	 */
	private String jdbcDriverClassName;
	private String jdbcLogin;
	private String jdbcPass;
	private String jdbcUrl;
	private String jdbcSchema;
	private DatabaseManager dbm;

	public FAQ() {
		// read and set properties values
		setFieldValues();
		// instantiate a database manager to handle database connection pooling and credentials
		dbm = new DatabaseManager(jdbcDriverClassName, jdbcLogin, jdbcPass, jdbcUrl, jdbcSchema);
	}

	@Override
	protected void initResources() {
		getResourceConfig().register(Resource.class);
	}

	// //////////////////////////////////////////////////////////////////////////////////////
	// Service methods.
	// //////////////////////////////////////////////////////////////////////////////////////
	@Path("/") // this is the root resource
	public static class Resource {
		private FAQ service = (FAQ) Context.getCurrent().getService();

		/**
		 * 
		 * listAll Lists all FAQ entries.
		 * 
		 * 
		 * @return Response
		 * 
		 */
		@GET
		@Path("/list")
		@Produces(MediaType.APPLICATION_JSON)
		@Consumes(MediaType.TEXT_PLAIN)
		@ApiResponses(
				value = { @ApiResponse(
						code = HttpURLConnection.HTTP_OK,
						message = "listAll"),
						@ApiResponse(
								code = HttpURLConnection.HTTP_INTERNAL_ERROR,
								message = "err") })
		@ApiOperation(
				value = "listAll",
				notes = "")
		public Response listAll() {

			// listAll
			boolean listAll_condition = true;
			ResultSet result;

			try {

				String retrieveSQL = "SELECT * FROM faq.entry";
				PreparedStatement preparedStatement = service.dbm.getConnection().prepareStatement(retrieveSQL);
				result = preparedStatement.executeQuery();

			} catch (SQLException e) {

				e.printStackTrace();
				listAll_condition = false;
				result = null;

			}

			JSONArray entryList = new JSONArray();
			try {
				while (result.next()) {
					JSONObject entry = new JSONObject();
					entry.put("id", result.getObject("ID"));
					entry.put(QUESTION_KEY, result.getObject(QUESTION_KEY));
					entry.put(ANSWER_KEY, result.getObject(ANSWER_KEY));
					entryList.add(entry);
				}
			} catch (SQLException e) {
				listAll_condition = false;
				e.printStackTrace();
			}

			if (listAll_condition) {
				Response listAll = Response.status(Status.OK).entity(entryList.toJSONString()).build();
				return listAll;

			} else {
				// err
				JSONObject errResult = new JSONObject();
				Response err = Response.status(Status.INTERNAL_SERVER_ERROR).entity(errResult.toJSONString()).build();
				return err;
			}
		}

		/**
		 * getEntry Get a single entry.
		 * 
		 * @param id The id of the entry you want to retrieve.
		 * 
		 * @return Response
		 */
		@GET
		@Path("{id}")
		@Produces(MediaType.APPLICATION_JSON)
		@ApiResponses(
				value = { @ApiResponse(
						code = HttpURLConnection.HTTP_OK,
						message = "get") })
		@ApiOperation(
				value = "getEntry",
				notes = "")
		public Response getEntry(@PathParam("id") int id) {

			boolean entryGet_conditon = true;
			boolean errorCondition = false;
			ResultSet result;
			JSONObject res = new JSONObject();

			try {
				String getSQL = "SELECT * FROM faq.entry WHERE id=?";
				PreparedStatement preparedStatement = service.dbm.getConnection().prepareStatement(getSQL);
				preparedStatement.setInt(1, id);
				result = preparedStatement.executeQuery();
				System.out.println(result.first());
				res.put(QUESTION_KEY, result.getObject(QUESTION_KEY));
				res.put(ANSWER_KEY, result.getObject(ANSWER_KEY));

			} catch (SQLException e) {
				e.printStackTrace();
				entryGet_conditon = false;
			} catch (Exception e) {
				e.printStackTrace();
				errorCondition = true;
			}

			if (entryGet_conditon) {
				Response get = Response.status(Status.OK).entity(res.toJSONString()).build();
				return get;
			}
			if (errorCondition) {
				// err
				JSONObject errResult = new JSONObject();
				errResult.put("message", "Internal Error!");
				Response err = Response.status(Status.INTERNAL_SERVER_ERROR).entity(errResult.toJSONString()).build();
				return err;
			} else {
				// err
				JSONObject errResult = new JSONObject();
				errResult.put("message", "SQL error, object not found!");
				Response err = Response.status(Status.INTERNAL_SERVER_ERROR).entity(errResult.toJSONString()).build();
				return err;
			}

		}

		/**
		 * 
		 * createEntry Create a FAQ entry.
		 * 
		 * @param entry a JSONObject
		 * 
		 * @return Response
		 * 
		 */
		@POST
		@Path("/create")
		@Produces(MediaType.APPLICATION_JSON)
		@Consumes(MediaType.APPLICATION_JSON)
		@ApiResponses(
				value = { @ApiResponse(
						code = HttpURLConnection.HTTP_CREATED,
						message = "entryCreated") })
		@ApiOperation(
				value = "createEntry",
				notes = "")
		public Response createEntry(String entry) {
			JSONObject entry_JSON = (JSONObject) JSONValue.parse(entry);

			boolean entryCreated_condition = true;
			int id = -1;

			// entryCreated
			try {
				String insertSQL = "INSERT INTO faq.entry (answer,question) VALUES (?,?)";
				PreparedStatement preparedStatement = service.dbm.getConnection().prepareStatement(insertSQL,
						Statement.RETURN_GENERATED_KEYS);
				preparedStatement.setString(2, entry_JSON.get(QUESTION_KEY).toString());
				preparedStatement.setString(1, entry_JSON.get(ANSWER_KEY).toString());
				preparedStatement.executeUpdate();
				ResultSet rs;
				rs = preparedStatement.getGeneratedKeys();
				rs.next();
				id = rs.getInt(1);
			} catch (SQLException e) {
				e.printStackTrace();
				entryCreated_condition = false;
			}

			if (entryCreated_condition) {
				JSONObject result = new JSONObject();
				result.put("Result:", "Success");
				result.put("id", id);
				Response entryCreated = Response.status(Status.CREATED).entity(result.toJSONString()).build();
				return entryCreated;
			} else {
				// err
				JSONObject errResult = new JSONObject();
				errResult.put("message", "Internal Error!");
				Response err = Response.status(Status.INTERNAL_SERVER_ERROR).entity(errResult.toJSONString()).build();
				return err;
			}
		}

		/**
		 * deleteEntry Delete a FAQ entry.
		 * 
		 * @param entry a JSONObject containing the id of the entry to be deleted
		 * 
		 * @return Response
		 */
		@DELETE
		@Path("/delete")
		@Produces(MediaType.APPLICATION_JSON)
		@Consumes(MediaType.APPLICATION_JSON)
		@ApiResponses(
				value = { @ApiResponse(
						code = HttpURLConnection.HTTP_CREATED,
						message = "entryCreated") })
		@ApiOperation(
				value = "deleteEntry",
				notes = "")
		public Response deleteEntry(String entry) {
			JSONObject entry_JSON = (JSONObject) JSONValue.parse(entry);
			boolean entryDeleted_condition = true;
			boolean errorCondition = false;
			// entryDeleted
			try {
				String deleteSQL = "DELETE FROM entry WHERE entry.id=?";
				PreparedStatement preparedStatement = service.dbm.getConnection().prepareStatement(deleteSQL);
				preparedStatement.setString(1, entry_JSON.get("id").toString());
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				e.printStackTrace();
				entryDeleted_condition = false;
			} catch (Exception e) {
				e.printStackTrace();
				errorCondition = true;
			}

			if (entryDeleted_condition) {
				Response entryDeleted = Response.status(Status.NO_CONTENT).entity("").build();
				return entryDeleted;
			}
			if (errorCondition) {
				// err
				JSONObject errResult = new JSONObject();
				errResult.put("message", "Internal Error!");
				Response err = Response.status(Status.INTERNAL_SERVER_ERROR).entity(errResult.toJSONString()).build();
				return err;
			} else {
				// err
				JSONObject errResult = new JSONObject();
				errResult.put("message", "SQL Error, object not found!");
				Response err = Response.status(Status.INTERNAL_SERVER_ERROR).entity(errResult.toJSONString()).build();
				return err;
			}
		}

		// //////////////////////////////////////////////////////////////////////////////////////
		// Methods required by the LAS2peer framework.
		// //////////////////////////////////////////////////////////////////////////////////////
		/**
		 * 
		 * Returns the API documentation of all annotated resources for purposes of Swagger documentation.
		 * 
		 * @return The resource's documentation
		 * 
		 */
		@GET
		@Path("/swagger.json")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getSwaggerJSON() {
			Swagger swagger = new Reader(new Swagger()).read(this.getClass());
			if (swagger == null) {
				return Response.status(Status.NOT_FOUND).entity("Swagger API declaration not available!").build();
			}
			try {
				return Response.status(Status.OK).entity(Json.mapper().writeValueAsString(swagger)).build();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
			}
		}
	}
}