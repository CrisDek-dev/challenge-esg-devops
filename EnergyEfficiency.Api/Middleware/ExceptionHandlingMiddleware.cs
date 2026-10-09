using System.Net;
using System.Text.Json;
using EnergyEfficiency.Api.Exceptions;

namespace EnergyEfficiency.Api.Middleware;

public class ExceptionHandlingMiddleware
{
    private readonly RequestDelegate _next;
    private readonly ILogger<ExceptionHandlingMiddleware> _logger;

    public ExceptionHandlingMiddleware(RequestDelegate next, ILogger<ExceptionHandlingMiddleware> logger)
    {
        _next = next;
        _logger = logger;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        try
        {
            await _next(context);
        }
        catch (NotFoundException ex)
        {
            await WriteProblemAsync(context, HttpStatusCode.NotFound, "Recurso nao encontrado", ex.Message);
        }
        catch (BusinessException ex)
        {
            await WriteProblemAsync(context, HttpStatusCode.BadRequest, "Regra de negocio violada", ex.Message);
        }
        catch (Exception ex)
        {
            _logger.LogError(ex, "Erro nao tratado ao processar {Path}", context.Request.Path);
            await WriteProblemAsync(context, HttpStatusCode.InternalServerError, "Erro interno",
                "Ocorreu um erro inesperado ao processar a requisicao.");
        }
    }

    private static async Task WriteProblemAsync(HttpContext context, HttpStatusCode status, string titulo, string detalhe)
    {
        if (context.Response.HasStarted) return;

        context.Response.Clear();
        context.Response.StatusCode = (int)status;
        context.Response.ContentType = "application/problem+json";

        var payload = new
        {
            type = $"https://httpstatuses.com/{(int)status}",
            title = titulo,
            status = (int)status,
            detail = detalhe,
            instance = context.Request.Path.Value
        };

        var json = JsonSerializer.Serialize(payload, new JsonSerializerOptions { PropertyNamingPolicy = JsonNamingPolicy.CamelCase });
        await context.Response.WriteAsync(json);
    }
}
