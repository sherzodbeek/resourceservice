# resourceservice
Resource Service

## Sub-task 1: Resource Service

For a **Resource Service**, it is recommended to implement a service with CRUD operations for processing mp3 files.

**Service definition could be next:**

<table>
    <tr>
        <td><b>POST /resources</b></td>
        <td colspan="6"><b>Upload new resource</b></td>
    </tr>
    <tr>
        <td rowspan="2"><b>Request</b></td>
        <td><i>Parameter</i></td>
        <td><i>Description</i></td>
        <td><i>Restriction</i></td>
        <td><i>Body example</i></td>
        <td><i>Description</i></td>
        <td><i>Restriction</i></td>
    </tr>
    <tr>
        <td></td>
        <td></td>
        <td></td>
        <td>Audio data binary</td>
        <td>Content type – audio/mpeg</td>
        <td>MP3 audio data</td>
    </tr>
    <tr>
        <td rowspan="2"><b>Response</b></td>
        <td><i>Body</i></td>
        <td><i>Description</i></td>
        <td colspan="4"><i>Code</i></td>
    </tr>
    <tr>
        <td><p>{</p>
            <p>&nbsp;&nbsp;&nbsp;&nbsp;"id":1123</p>
            <p>}</p>
        </td>
        <td>Integer id — ID of the created resource</td>
        <td colspan="4"><p>200 – OK</p>
                        <p>400 – Validation failed or request body is invalid MP3</p>
                        <p>500 – An internal server error has occurred</p>
        </td>
    </tr>
    <tr>
        <td><b>GET /resources/{id}</b></td>
        <td colspan="6"><b>Get the binary audio data of a resource</b></td>
    </tr>
    <tr>
        <td rowspan="3"><b>Request</b></td>
        <td><i>Parameter</i></td>
        <td><i>Description</i></td>
        <td><i>Restriction</i></td>
        <td><i>Body example</i></td>
        <td><i>Description</i></td>
        <td><i>Restriction</i></td>
    </tr>
    <tr>
        <td>Integer id</td>
        <td>The ID of the resource to get</td>
        <td>ID of an existing resource</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
    </tr>
    <tr>
        <td rowspan="2"><b>Response</b></td>
        <td><i>Body</i></td>
        <td><i>Description</i></td>
        <td colspan="4"><i>Code</i></td>
    </tr>
    <tr>
        <td>Audio bytes</td>
        <td></td>
        <td colspan="4"><p>200 – OK</p>
                        <p>404 – The resource with the specified id does not exist</p>
                        <p>500 – An internal server error has occurred</p>
        </td>
    </tr>
    <tr>
        <td><b>DELETE /resources?id=1,2</b></td>
        <td colspan="6"><b>Delete a resource(s). If there is no resource for id, do nothing</b></td>
    </tr>
    <tr>
        <td rowspan="2"><b>Request</b></td>
        <td><i>Parameter</i></td>
        <td><i>Description</i></td>
        <td><i>Restriction</i></td>
        <td><i>Body example</i></td>
        <td><i>Description</i></td>
        <td><i>Restriction</i></td>
    </tr>
    <tr>
        <td>String id</td>
        <td>CSV (Comma Separated Values) of resource IDs to remove</td>
        <td>Valid CSV length < 200 characters</td>
        <td></td>
        <td></td>
        <td></td>
    </tr>
    <tr>
        <td rowspan="2"><b>Response</b></td>
        <td><i>Body</i></td>
        <td><i>Description</i></td>
        <td colspan="4"><i>Code</i></td>
    </tr>
    <tr>
        <td><p>{</p>
            <p>&nbsp;&nbsp;&nbsp;&nbsp;"ids": [1,2]</p>
            <p>}</p>
        </td>
        <td>Integer [] ids – ids of deleted resources</td>
        <td colspan="4"><p>200 – OK</p>
                        <p>500 – An internal server error has occurred</p>
        </td>
    </tr>
</table>

When uploading a mp3 file, the **Resource Service** should process the file in this way:
- Extract file metadata. An external library can be used for this purpose.(e.g. [Apache Tika](https://www.tutorialspoint.com/tika/tika_extracting_mp3_files.htm)).
- Store mp3 file to the underlying database of the service as Blob.
- Invoke **Song Service** to save mp3 file metadata.