using Newtonsoft.Json;
using JsonSerializer = Newtonsoft.Json.JsonSerializer;

// ReSharper disable InconsistentNaming

namespace Web.NetInteraction
{
    public enum AuthResult
    {
        AUTH_SUCCESS,
        INVALID_PASSWORD,
        ERROR_PROCESSING,
        EMAIL_WAITING_CODE,
        USER_NOT_REGISTERED
    }

    public class AuthResultConverter : JsonConverter<AuthResult>
    {
        public override AuthResult ReadJson(JsonReader reader, Type objectType, AuthResult existingValue, bool hasExistingValue, JsonSerializer serializer)
        {
            var value = reader.Value?.ToString();
            return value switch
            {
                "AUTH_SUCCESS" => AuthResult.AUTH_SUCCESS,
                "INVALID_PASSWORD" => AuthResult.INVALID_PASSWORD,
                "ERROR_PROCESSING" => AuthResult.ERROR_PROCESSING,
                "EMAIL_WAITING_CODE" => AuthResult.EMAIL_WAITING_CODE,
                "USER_NOT_REGISTERED" => AuthResult.USER_NOT_REGISTERED,
                _ => throw new JsonSerializationException($"Неизвестное значение для AuthResult: {value}")
            };
        }

        public override void WriteJson(JsonWriter writer, AuthResult value, JsonSerializer serializer)
        {
            writer.WriteValue(value.ToString());
        }
    }
}
